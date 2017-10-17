package cn.htd.promotion.cpc.biz.service.impl;

import cn.htd.promotion.cpc.biz.dao.*;
import cn.htd.promotion.cpc.biz.service.VoteActivityFansVoteService;
import cn.htd.promotion.cpc.common.constants.RedisConst;
import cn.htd.promotion.cpc.common.emums.ResultCodeEnum;
import cn.htd.promotion.cpc.common.util.ExecuteResult;
import cn.htd.promotion.cpc.common.util.PromotionRedisDB;
import cn.htd.promotion.cpc.dto.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public ExecuteResult<String> validateFansVoteNumByDayAndStore(Long voteActivityId, Long fansId, String memberCode, Date date) {
        ExecuteResult<String> executeResult = new ExecuteResult<>();
        VoteActivityResDTO voteActivityResDTO = this.voteActivityDAO.selectByPrimaryKey(voteActivityId);
        if (voteActivityResDTO != null) {
            Integer voteNumPAccountPDayPStoreLimit = voteActivityResDTO.getVoteNumPAccountPDayPStore(); // 粉丝当前单个门店投票数量上限
            int voteNumByDayAndStore = this.queryFansVoteNumByDayAndStore(voteActivityId, fansId, memberCode, date);
            // 投票前校验：当前投票数小于限制数，返回校验通过
            if (voteNumByDayAndStore < voteNumPAccountPDayPStoreLimit) {
                executeResult.setCode(ResultCodeEnum.SUCCESS.getCode());
                executeResult.setResultMessage("校验成功");
                return executeResult;
            } else {
                executeResult.setCode(ResultCodeEnum.SUCCESS.getCode());
                executeResult.setResultMessage("您今天已经投过我了，谢谢您！");
                return executeResult;
            }
        } else {
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST.getCode());
            executeResult.setResultMessage("投票活动不存在");
            return executeResult;
        }
    }

    @Override
    public ExecuteResult<String> validateFansVoteStoreNumByDay(Long voteActivityId, Long fansId, Date date) {
        ExecuteResult<String> executeResult = new ExecuteResult<>();
        VoteActivityResDTO voteActivityResDTO = this.voteActivityDAO.selectByPrimaryKey(voteActivityId);
        if (voteActivityResDTO != null) {
            Integer voteSotreNumPAccountPDayLimit = voteActivityResDTO.getVoteSotreNumPAccountPDay(); // 粉丝投门店数量上限
            int voteSotreNumByDay = this.queryFansVoteStoreNumByDay(voteActivityId, fansId, date);
            // 投票前校验：当前投票数小于限制数，返回校验通过
            if (voteSotreNumByDay < voteSotreNumPAccountPDayLimit) {
                executeResult.setCode(ResultCodeEnum.SUCCESS.getCode());
                executeResult.setResultMessage("校验成功");
                return executeResult;
            } else {
                executeResult.setCode(ResultCodeEnum.SUCCESS.getCode());
                executeResult.setResultMessage("您今天已经达到每日可投票门店数上限，明天再来吧！");
                return executeResult;
            }
        } else {
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST.getCode());
            executeResult.setResultMessage("投票活动不存在");
            return executeResult;
        }
    }

    @Transactional
    @Override
    public ExecuteResult<String> voteByFans(Long voteActivityId, Long fansId, String memberCode) {
        logger.info("开始投票, 活动ID:{},粉丝ID:{},会员店编码:{}", voteActivityId, fansId, memberCode);
        ExecuteResult<String> executeResult = new ExecuteResult<>();
        Date date = new Date();
        // 在REIDS插入投票记录
        String key = RedisConst.FANS_VOTE_HASH_PREFIX + REDIS_SEPARATOR + voteActivityId + REDIS_SEPARATOR + fansId + REDIS_SEPARATOR + SP.format(date);
        String field = memberCode;
        if (promotionRedisDB.exists(key) && promotionRedisDB.existsHash(key, field)) {
            Long returnValue = promotionRedisDB.incrHash(key, field);
            logger.info("操作【REDIS-incrHash】, key :{}, field:{}, returnValue:{}", key, field, returnValue);
        } else {
            promotionRedisDB.setHash(key, field, "1");
            logger.info("操作【REDIS-setHash】, key :{}, field:{}, value:{}", key, field, 1);
        }
        // 根据voteActivityId和memberCode查询voteActivityMemberResDTO
        VoteActivityMemberResDTO voteActivityMemberResDTO = this.voteActivityMemberDAO.selectByVoteIdAndMemberCode(voteActivityId, memberCode);
        if (voteActivityMemberResDTO == null) {
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
            executeResult.setResultMessage("根据活动ID:" + voteActivityId+ "和memberCode:" + memberCode + "查询不到会员店报名信息");
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
        return executeResult;
    }

    @Override
    public ExecuteResult<String> forwardByFans(Long voteActivityId, String memberCode) {
        logger.info("开始转发, 活动ID:{}, 会员店编码:{}", voteActivityId, memberCode);
        ExecuteResult<String> executeResult = new ExecuteResult<>();
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
        return executeResult;
    }

    @Override
    public ExecuteResult<String> isShowVoteActivityByMemberCode(String memberCode) {
        ExecuteResult<String> executeResult = new ExecuteResult<>();
        // 有没有所处当前时间的投票活动
        VoteActivityResDTO voteActivityResDTO = this.voteActivityDAO.selectCurrentActivity();
        if (voteActivityResDTO != null) {
            Long voteId = voteActivityResDTO.getVoteId();
            // 校验会员店报名情况，是否审核通过
            VoteActivityMemberResDTO voteActivityMemberResDTO = this.voteActivityMemberDAO.selectByVoteIdAndMemberCode(voteId, memberCode);
            // 状态是否是已审核和已报名
            if (voteActivityMemberResDTO == null) {
                executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
                executeResult.setResultMessage("根据活动ID:" + voteId+ "和memberCode:" + memberCode + "查询不到会员店报名信息");
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
                executeResult.setResultMessage("可以展示投票活动");
                return executeResult;
            }
        } else {
            executeResult.setCode(ResultCodeEnum.OTE_ACTIVITY_HAVA_NO_VOTE_ACTIVITY.getCode());
            executeResult.setResultMessage("当前没有投票活动");
            return executeResult;
        }
    }

    // isShowVoteActivityByMemberCode为true的前提
    @Override
    public ExecuteResult<VoteActivityMemberVoteDetailDTO> getMemberVoteDetail(Long voteActivityId, String memberCode) {
        ExecuteResult<VoteActivityMemberVoteDetailDTO> executeResult = new ExecuteResult<>();
        VoteActivityMemberVoteDetailDTO voteActivityMemberVoteDetailDTO = new VoteActivityMemberVoteDetailDTO();
        // 查询活动
        VoteActivityResDTO voteActivityResDTO = this.voteActivityDAO.selectByPrimaryKey(voteActivityId);
        if (voteActivityResDTO == null) {
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST.getCode());
            executeResult.setResultMessage("查询不到该活动，voteActivityId：" + voteActivityId);
            return executeResult;
        }
        // 查询报名信息
        VoteActivityMemberResDTO voteActivityMemberResDTO = this.voteActivityMemberDAO.selectByVoteIdAndMemberCode(voteActivityId, memberCode);
        if (voteActivityMemberResDTO == null) {
            executeResult.setCode(ResultCodeEnum.VOTE_ACTIVITY_NOT_EXIST_MEMBER.getCode());
            executeResult.setResultMessage("查询不到该会员店的报名信息，voteActivityId：" + voteActivityId + ",memberCode:" + memberCode);
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
        voteActivityMemberVoteDetailDTO.setVoteEndTime(voteActivityResDTO.getVoteEndTime());
        voteActivityMemberVoteDetailDTO.setSystemTime(systemTime);
        voteActivityMemberVoteDetailDTO.setMemberActivityDec(voteActivityMemberResDTO.getMemberActivityDec());
        // 获取图片
        List<VoteActivityMemberPictureResDTO> voteActivityMemberPictureResDTOList = this.voteActivityMemberPictureDAO.selectByVoteMemberId(voteActivityMemberResDTO.getVoteMemberId());
        voteActivityMemberVoteDetailDTO.setVoteActivityMemberPictureResDTOList(voteActivityMemberPictureResDTOList);
        // 获取投票排名top10
        List<VoteActivityMemberRankingDTO> voteActivityMemberRankingDTOList = new ArrayList<>();
        List<HashMap<String, String>> rankingList = this.voteActivityMemberDAO.selectMemberRankingTop10(voteActivityId);
        for (HashMap<String, String> hashMap : rankingList) {
            VoteActivityMemberRankingDTO voteActivityMemberRankingDTO = new VoteActivityMemberRankingDTO();
            voteActivityMemberRankingDTO.setMemberName(hashMap.get("member_name"));
            voteActivityMemberVoteDetailDTO.setRanking(Integer.valueOf(hashMap.get("rowNum")));
            voteActivityMemberVoteDetailDTO.setVoteNum(Integer.valueOf(hashMap.get("voteNum")));
            voteActivityMemberRankingDTOList.add(voteActivityMemberRankingDTO);
        }
        // 获取当前会员店排名情况
        HashMap<String, String> rankingByMemberCode = this.voteActivityMemberDAO.selectMemberRankingByMemberCode(voteActivityId, memberCode);
        if (rankingByMemberCode != null) {
            voteActivityMemberVoteDetailDTO.setRanking(Integer.valueOf(rankingByMemberCode.get("rowNum")));
            voteActivityMemberVoteDetailDTO.setVoteNum(Integer.valueOf(rankingByMemberCode.get("voteNum")));
        }
        executeResult.setResult(voteActivityMemberVoteDetailDTO);
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
        String key = RedisConst.FANS_VOTE_HASH_PREFIX + REDIS_SEPARATOR + voteActivityId + REDIS_SEPARATOR + fansId + REDIS_SEPARATOR + SP.format(date);
        String field = memberCode;
        int count = 0;
        if (promotionRedisDB.exists(key) && promotionRedisDB.existsHash(key, field)) {
            String value = promotionRedisDB.getHash(key, field);
            count = Integer.valueOf(value);
        }
        return count;
    }

    private int queryFansVoteStoreNumByDay(Long voteActivityId, Long fansId, Date date) {
        String key = RedisConst.FANS_VOTE_HASH_PREFIX + REDIS_SEPARATOR + voteActivityId + REDIS_SEPARATOR + fansId + REDIS_SEPARATOR + SP.format(date);
        int count = 0;
        if (promotionRedisDB.exists(key)) {
            count = promotionRedisDB.getHLen(key);
        }
        return count;
    }
}
