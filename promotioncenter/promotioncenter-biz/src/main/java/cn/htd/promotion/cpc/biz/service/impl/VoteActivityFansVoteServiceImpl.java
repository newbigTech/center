package cn.htd.promotion.cpc.biz.service.impl;

import cn.htd.promotion.cpc.biz.dao.*;
import cn.htd.promotion.cpc.biz.service.VoteActivityFansVoteService;
import cn.htd.promotion.cpc.common.constants.RedisConst;
import cn.htd.promotion.cpc.common.emums.ResultCodeEnum;
import cn.htd.promotion.cpc.common.util.ExecuteResult;
import cn.htd.promotion.cpc.common.util.PromotionRedisDB;
import cn.htd.promotion.cpc.dto.response.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 投票活动-粉丝投票相关服务
 * @author chenkang
 * @date 2017-10-12
 */
@Service("voteActivityFansVoteService")
public class VoteActivityFansVoteServiceImpl implements VoteActivityFansVoteService {
    /**
     * KEY分隔符
     */
    private static final String REDIS_SEPARATOR = ":";

    private static final SimpleDateFormat SP = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private PromotionRedisDB promotionRedisDB;

    @Resource
    private VoteActivityDAO voteActivityDAO;

    @Resource
    private VoteActivityMemberDAO voteActivityMemberDAO;

    @Resource
    private VoteActivityFansVoteDAO voteActivityFansVoteDAO;

    @Resource
    private VoteActivityFansForwardDAO voteActivityFansForwardDAO;

    @Resource
    private VoteActivityMemberPictureDAO voteActivityMemberPictureDAO;

    private Logger logger = LoggerFactory.getLogger(VoteActivityFansVoteServiceImpl.class);

    @Transactional
    @Override
    public ExecuteResult<String> voteByFans(Long voteActivityId, Long fansId, String memberCode) {
        logger.info("开始投票, 活动ID:{},粉丝ID:{},会员店编码:{}", voteActivityId, fansId, memberCode);
        ExecuteResult<String> executeResult = new ExecuteResult<>();
        if (voteActivityId == null || voteActivityId == 0 || fansId == null || fansId == 0 || StringUtils.isEmpty(memberCode)) {
            logger.info("必填参数为空");
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_INPUT_PARAM_IS_NULL.getCode());
            executeResult.setResultMessage(ResultCodeEnum.OTE_ACTIVITY_INPUT_PARAM_IS_NULL.getMsg());
            return executeResult;
        }
        Date date = new Date();
        // 校验投票限制
        VoteActivityResDTO voteActivityResDTO = this.voteActivityDAO.selectByPrimaryKey(voteActivityId);
        if (voteActivityResDTO == null) {
            logger.info("投票活动不存在");
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST.getCode());
            executeResult.setResultMessage("投票活动不存在");
            return executeResult;
        }
        Date voteSignUpStartTime = voteActivityResDTO.getVoteSignUpStartTime();
        Date voteEndTime = voteActivityResDTO.getVoteEndTime();
        if (voteSignUpStartTime.after(date)) { // 当前时间不在活动时间内
            logger.info("当前投票活动未开始, voteActivityId:{}", voteActivityId);
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST.getCode());
            executeResult.setResultMessage("投票活动未开始");
            return executeResult;
        }
        if(voteEndTime.before(date)) {
            logger.info("当前投票活动已结束, voteActivityId:{}", voteActivityId);
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST.getCode());
            executeResult.setResultMessage("投票活动已结束");
            return executeResult;
        }
        Integer voteNumPAccountPDayPStoreLimit = voteActivityResDTO.getVoteNumPAccountPDayPStore(); // 粉丝当前单个门店投票数量上限
        int voteNumByDayAndStore = this.queryFansVoteNumByDayAndStore(voteActivityId, fansId, memberCode, date);
        // 投票前校验：当前投票数小于限制数，返回校验通过
        if (voteNumByDayAndStore >= voteNumPAccountPDayPStoreLimit) {
            logger.info("您今天已经投过我了，谢谢您！");
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_NOT_MEET_VOTE_NUM_PER_STORE.getCode());
            executeResult.setResultMessage("您今天已经投过我了，谢谢您！");
            return executeResult;
        }
        int voteSotreNumByDay = this.queryFansVoteStoreNumByDay(voteActivityId, fansId, date);
        Map<Object, Object> storeMap = this.queryFansVoteStoreMap(voteActivityId, fansId, date);
        // 投票前校验：当前投票数小于限制数，返回校验通过
        Integer voteSotreNumPAccountPDayLimit = voteActivityResDTO.getVoteSotreNumPAccountPDay(); // 粉丝投门店数量上限
        if (voteSotreNumByDay >= voteSotreNumPAccountPDayLimit && !storeMap.containsKey(memberCode)) { // 已经投过这个门店的，就不校验了
            logger.info("您今天已经达到每日可投票门店数上限，明天再来吧！");
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_NOT_MEET_VOTE_STORE_NUM.getCode());
            executeResult.setResultMessage("您今天已经达到每日可投票门店数上限，明天再来吧！");
            return executeResult;
        }
        // 根据voteActivityId和memberCode查询voteActivityMemberResDTO
        VoteActivityMemberResDTO voteActivityMemberResDTO = this.voteActivityMemberDAO.selectByVoteIdAndMemberCode(voteActivityId, memberCode);
        if (voteActivityMemberResDTO == null) {
            logger.info("根据活动ID:" + voteActivityId+ "和memberCode:" + memberCode + "查询不到会员店报名信息");
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
            executeResult.setResultMessage("根据活动ID:" + voteActivityId+ "和memberCode:" + memberCode + "查询不到会员店报名信息");
            return executeResult;
        }
        if (voteActivityMemberResDTO.getSignStatus() != 1) {
            logger.info("根据活动ID:" + voteActivityId+ "和memberCode:" + memberCode + "会员店未报名");
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
            executeResult.setResultMessage("会员店:" + memberCode + "未报名");
            return executeResult;
        }
        if (voteActivityMemberResDTO.getAuditStatus() != 1) {
            logger.info("根据活动ID:" + voteActivityId+ "和memberCode:" + memberCode + "会员店未审核通过");
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
            executeResult.setResultMessage("会员店:" + memberCode + "未审核通过");
            return executeResult;
        }
        // 开始投票
        // 在REIDS插入投票记录
        String key = RedisConst.FANS_VOTE_HASH_PREFIX + REDIS_SEPARATOR + SP.format(date)+ REDIS_SEPARATOR + voteActivityId + REDIS_SEPARATOR + fansId;
        String field = memberCode;
        Long returnValue = promotionRedisDB.incrHash(key, field); // 增长，并且获取增长后的值
        promotionRedisDB.expire(key,  24 * 60 * 60); // 添加KEY的过期时间
        logger.info("操作【REDIS-incrHash】, key :{}, field:{}, returnValue:{}", key, field, returnValue);
        // 【关键】并发下，多个线程会突破前面的查询校验，利用增长后的值再次做判断
        if (returnValue > voteNumPAccountPDayPStoreLimit) { // 如果增长后超过了限制，做回滚
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_NOT_MEET_VOTE_NUM_PER_STORE.getCode());
            executeResult.setResultMessage("您今天已经投过我了，谢谢您！");
            // 回滚投票
            returnValue = promotionRedisDB.decrHash(key, field); // 增长后的值
            logger.info("操作【REDIS-incrHash】后，发现被并发，回滚投票的reids记录,, key :{}, field:{}, returnValue:{}", key, field, returnValue);
            return executeResult;
        }

        // 在数据库记录投票
        Long voteMemberId = voteActivityMemberResDTO.getVoteMemberId();
        VoteActivityFansVoteResDTO record = new VoteActivityFansVoteResDTO();
        record.setFansId(fansId);
        record.setVoteMemberId(voteMemberId); // 关联某个会员店和活动
        record.setFansSignUpTime(date);
        record.setCreateId(fansId);
        record.setCreateName(fansId.toString());
        record.setCreateTime(date);
        record.setModifyId(fansId);
        record.setModifyName(fansId.toString());
        record.setModifyTime(date);
        this.voteActivityFansVoteDAO.insert(record);
        // 更新最近投票时间BY voteMemberId
        VoteActivityMemberResDTO voteActivityMemberResDTOUpdate = new VoteActivityMemberResDTO();
        voteActivityMemberResDTOUpdate.setVoteMemberId(voteMemberId);
        voteActivityMemberResDTOUpdate.setMemberVoteLastTime(date);
        this.voteActivityMemberDAO.updateByPrimaryKeySelective(voteActivityMemberResDTOUpdate);
        executeResult.setResultMessage("投票成功");
        return executeResult;
    }

    @Override
    public ExecuteResult<String> forwardByFans(Long voteActivityId, String memberCode) {
        logger.info("开始转发, 活动ID:{}, 会员店编码:{}", voteActivityId, memberCode);
        ExecuteResult<String> executeResult = new ExecuteResult<>();
        if (voteActivityId == null || voteActivityId == 0 || StringUtils.isEmpty(memberCode)) {
            logger.info("必填参数为空");
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_INPUT_PARAM_IS_NULL.getCode());
            executeResult.setResultMessage(ResultCodeEnum.OTE_ACTIVITY_INPUT_PARAM_IS_NULL.getMsg());
            return executeResult;
        }
        Date date = new Date();
        // 根据voteActivityId和memberCode查询voteActivityMemberResDTO
        VoteActivityMemberResDTO voteActivityMemberResDTO = this.voteActivityMemberDAO.selectByVoteIdAndMemberCode(voteActivityId, memberCode);
        if (voteActivityMemberResDTO == null) {
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
            executeResult.setResultMessage("根据活动ID:" + voteActivityId+ "和memberCode:" + memberCode + "查询不到会员店报名信息");
            return executeResult;
        }
        // 在数据库记录投票
        Long voteMemberId = voteActivityMemberResDTO.getVoteMemberId();
        VoteActivityFansForwardResDTO record = new VoteActivityFansForwardResDTO();
        record.setVoteMemberId(voteMemberId); // 关联某个会员店和活动
        record.setCreateId(0L);
        record.setCreateName("SYSYTEM");
        record.setCreateTime(date);
        record.setModifyId(0L);
        record.setModifyName("SYSYTEM");
        record.setModifyTime(date);
        this.voteActivityFansForwardDAO.insert(record);
        executeResult.setResultMessage("转发成功");
        return executeResult;
    }

    @Override
    public ExecuteResult<Long> isShowVoteActivityByMemberCode(String memberCode) {
        ExecuteResult<Long> executeResult = new ExecuteResult<>();
        if (StringUtils.isEmpty(memberCode)) {
            logger.info("必填参数为空");
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_INPUT_PARAM_IS_NULL.getCode());
            executeResult.setResultMessage(ResultCodeEnum.OTE_ACTIVITY_INPUT_PARAM_IS_NULL.getMsg());
            return executeResult;
        }
        // 有没有所处当前时间的投票活动(投票开始的)
        VoteActivityResDTO voteActivityResDTO = this.voteActivityDAO.selectCurrentVoteStartActivity();
        if (voteActivityResDTO != null) {
            Long voteId = voteActivityResDTO.getVoteId();
            // 校验会员店报名情况，是否审核通过
            VoteActivityMemberResDTO voteActivityMemberResDTO = this.voteActivityMemberDAO.selectByVoteIdAndMemberCode(voteId, memberCode);
            // 状态是否是已审核和已报名
            if (voteActivityMemberResDTO == null) {
                executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
                executeResult.setResultMessage("当前活动下没有该会员店的信息, 活动ID：" + voteId + ", 会员店编码:" + memberCode);
                return executeResult;
            } else if (voteActivityMemberResDTO.getSignStatus() != 1) {
                executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_MEMBER_NOT_SIGN_UP.getCode());
                executeResult.setResultMessage("会员店未报名");
                return executeResult;
            } else if (voteActivityMemberResDTO.getAuditStatus() != 1) {
                executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_MEMBER_NOT_AUDIT_PASSED.getCode());
                executeResult.setResultMessage("会员店未通过审核");
                return executeResult;
            } else {
                executeResult.setCode(ResultCodeEnum.SUCCESS.getCode());
                executeResult.setResult(voteId);
                executeResult.setResultMessage("可以展示投票活动");
                return executeResult;
            }
        } else {
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_HAVA_NO_VOTE_ACTIVITY.getCode());
            executeResult.setResultMessage("当前没有开始的投票活动");
            return executeResult;
        }
    }

    // isShowVoteActivityByMemberCode为true的前提
    @Override
    public ExecuteResult<VoteActivityMemberVoteDetailDTO> getMemberVoteDetail(Long voteActivityId, String memberCode) {
        ExecuteResult<VoteActivityMemberVoteDetailDTO> executeResult = new ExecuteResult<>();
        if (voteActivityId == null || StringUtils.isEmpty(memberCode)) {
            logger.info("必填参数为空");
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_INPUT_PARAM_IS_NULL.getCode());
            executeResult.setResultMessage(ResultCodeEnum.OTE_ACTIVITY_INPUT_PARAM_IS_NULL.getMsg());
            return executeResult;
        }
        VoteActivityMemberVoteDetailDTO voteActivityMemberVoteDetailDTO = new VoteActivityMemberVoteDetailDTO();
        // 查询活动
        VoteActivityResDTO voteActivityResDTO = this.voteActivityDAO.selectByPrimaryKey(voteActivityId);
        if (voteActivityResDTO == null) {
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST.getCode());
            executeResult.setResultMessage("查询不到活动信息，活动ID：" + voteActivityId);
            return executeResult;
        }
        // 查询报名信息
        VoteActivityMemberResDTO voteActivityMemberResDTO = this.voteActivityMemberDAO.selectByVoteIdAndMemberCode(voteActivityId, memberCode);
        if (voteActivityMemberResDTO == null) {
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
            executeResult.setResultMessage("当前活动下没有该会员店的信息, 活动ID：" + voteActivityId + ", 会员店编码:" + memberCode);
            return executeResult;
        }
        // 组装结果集
        Date systemTime = new Date();
        voteActivityMemberVoteDetailDTO.setVoteId(voteActivityId);
        voteActivityMemberVoteDetailDTO.setVoteName(voteActivityResDTO.getVoteName());
        voteActivityMemberVoteDetailDTO.setVoteNumPAccountPDayPStore(voteActivityResDTO.getVoteNumPAccountPDayPStore());
        voteActivityMemberVoteDetailDTO.setVoteSotreNumPAccountPDay(voteActivityResDTO.getVoteSotreNumPAccountPDay());
        voteActivityMemberVoteDetailDTO.setMemberCode(memberCode);
        voteActivityMemberVoteDetailDTO.setMemberName(voteActivityMemberResDTO.getMemberName());
        voteActivityMemberVoteDetailDTO.setSignStatus(voteActivityMemberResDTO.getSignStatus());
        voteActivityMemberVoteDetailDTO.setAuditStatus(voteActivityMemberResDTO.getAuditStatus());
        voteActivityMemberVoteDetailDTO.setVoteEndTime(voteActivityResDTO.getVoteEndTime().getTime());
        voteActivityMemberVoteDetailDTO.setSystemTime(systemTime.getTime());
        voteActivityMemberVoteDetailDTO.setMemberActivityDec(voteActivityMemberResDTO.getMemberActivityDec());
        voteActivityMemberVoteDetailDTO.setVoteTopicPic(voteActivityResDTO.getVoteTopicPic());
        // 获取图片
        List<VoteActivityMemberPictureResDTO> voteActivityMemberPictureResDTOList = this.voteActivityMemberPictureDAO.selectByVoteMemberId(voteActivityMemberResDTO.getVoteMemberId());
        List<String> picList = new ArrayList<>();
        for (VoteActivityMemberPictureResDTO voteActivityMemberPictureResDTO : voteActivityMemberPictureResDTOList) {
            picList.add(voteActivityMemberPictureResDTO.getPictureUrl());
        }
        voteActivityMemberVoteDetailDTO.setVoteActivityMemberPictureResDTOList(picList);
        // 获取投票排名top10
        List<VoteActivityMemberRankingDTO> voteActivityMemberRankingDTOList = new ArrayList<>();
        List<HashMap<String, Object>> rankingList = this.voteActivityMemberDAO.selectMemberRankingTop10(voteActivityId);
        for (HashMap<String, Object> hashMap : rankingList) {
            VoteActivityMemberRankingDTO voteActivityMemberRankingDTO = new VoteActivityMemberRankingDTO();
            voteActivityMemberRankingDTO.setMemberName(String.valueOf(hashMap.get("member_name")));
            voteActivityMemberRankingDTO.setRowNum(((Long) hashMap.get("rowNum")).intValue());
            voteActivityMemberRankingDTO.setVotenum(((Long) hashMap.get("voteNum")).intValue());
            voteActivityMemberRankingDTOList.add(voteActivityMemberRankingDTO);
        }
        voteActivityMemberVoteDetailDTO.setVoteActivityMemberRankingDTOList(voteActivityMemberRankingDTOList);
        // 获取当前会员店排名情况
        HashMap<String, Object> rankingByMemberCode = this.voteActivityMemberDAO.selectMemberRankingByMemberCode(voteActivityId, memberCode);
        if (rankingByMemberCode != null) {
            voteActivityMemberVoteDetailDTO.setRanking(((Long) rankingByMemberCode.get("rowNum")).intValue());
            voteActivityMemberVoteDetailDTO.setVoteNum(((Long) rankingByMemberCode.get("voteNum")).intValue());
        }
        executeResult.setResult(voteActivityMemberVoteDetailDTO);
        executeResult.setResultMessage(ResultCodeEnum.SUCCESS.getMsg());
        return executeResult;
    }

    /**
     * REDIS缓存 粉丝投门店数记录哈希
     * 格式： {
     *          KEY ：  前缀+voteActivityId+粉丝ID+日期
     *          filed : memberCode
     *          value ： 投票次数
     *        }
     */

    /**
     * 查询粉丝当日投此门店票数
     * @param voteActivityId 投票活动ID
     * @param fansId 粉丝ID
     * @param memberCode 会员店编码
     * @param date 日期
     * @return
     */
    private int queryFansVoteNumByDayAndStore(Long voteActivityId, Long fansId, String memberCode, Date date) {
        String key = RedisConst.FANS_VOTE_HASH_PREFIX + REDIS_SEPARATOR + SP.format(date) + REDIS_SEPARATOR + voteActivityId + REDIS_SEPARATOR + fansId;
        String field = memberCode;
        int count = 0;
        if (promotionRedisDB.exists(key) && promotionRedisDB.existsHash(key, field)) {
            String value = promotionRedisDB.getHash(key, field);
            count = Integer.valueOf(value);
        }
        return count;
    }

    private int queryFansVoteStoreNumByDay(Long voteActivityId, Long fansId, Date date) {
        String key = RedisConst.FANS_VOTE_HASH_PREFIX + REDIS_SEPARATOR + SP.format(date) + REDIS_SEPARATOR + voteActivityId + REDIS_SEPARATOR + fansId;
        int count = 0;
        if (promotionRedisDB.exists(key)) {
            count = promotionRedisDB.getHLen(key);
        }
        return count;
    }

    private Map<Object, Object> queryFansVoteStoreMap(Long voteActivityId, Long fansId, Date date) {
        String key = RedisConst.FANS_VOTE_HASH_PREFIX + REDIS_SEPARATOR + SP.format(date) + REDIS_SEPARATOR + voteActivityId + REDIS_SEPARATOR + fansId;
        Map<Object, Object> map = new HashMap<>();
        if (promotionRedisDB.exists(key)) {
            map = promotionRedisDB.getHashALL(key);
        }
        return map;
    }
}
