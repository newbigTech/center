package cn.htd.promotion.cpc.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import cn.htd.common.DataGrid;
import cn.htd.common.Pager;
import cn.htd.promotion.cpc.biz.convert.BuyerWinningRecordConvert;
import cn.htd.promotion.cpc.biz.dao.BuyerWinningRecordDAO;
import cn.htd.promotion.cpc.biz.dmo.BuyerWinningRecordDMO;
import cn.htd.promotion.cpc.biz.service.AwardRecordService;
import cn.htd.promotion.cpc.dto.request.PromotionAwardReqDTO;
import cn.htd.promotion.cpc.dto.response.PromotionAwardDTO;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by tangjiayong on 2017/8/22.
 */
@Service("awardRecordService")
public class AwardRecordServiceImpl implements AwardRecordService {

    private static final Logger logger = LoggerFactory.getLogger(AwardRecordServiceImpl.class);

    @Resource
    public BuyerWinningRecordDAO awardRecordDAO;

    @Override
    public DataGrid<PromotionAwardDTO> getAwardRecordByPromotionId(PromotionAwardReqDTO dto, String messageId) {
        logger.info("promotionId{}:AwardRecordServiceImpl.getAwardRecordByPromotionId（）方法,入参{}", messageId,
                JSON.toJSONString(dto));

        //分页
        Pager<PromotionAwardReqDTO> page = new Pager<PromotionAwardReqDTO>();
        page.setPage(dto.getPage());
        page.setRows(dto.getPageSize());

//        if (dto.getPage() < 1) {
//            page.setPage(1);
//        }
//        if (dto.getPageSize() < 1) {
//            page.setRows(10);
//        }

        // 开始时间
        String startTime = dto.getWinningStartTime();
        // 结束时间
        String endTime = dto.getWinningEndTime();

        if (null != startTime && !"".equals(startTime)) {
            dto.setWinningStartTime(startTime + " 00:00:00");
        }
        if (null != endTime && !"".equals(endTime)) {
            dto.setWinningEndTime(endTime + " 23:59:59");
        }



        DataGrid<PromotionAwardDTO> dataGrid = new DataGrid<PromotionAwardDTO>();
        try {
            List<BuyerWinningRecordDMO> list = null;
            long count =0;
            if("hl".equals(dto.getSource())){
                list = awardRecordDAO.getAwardRecordByPromotionId(dto, page);
                count = awardRecordDAO.getTotalAwardRecord(dto);
            }else {
                list = awardRecordDAO.getAwardRecordForBoss(dto, page);
                count = awardRecordDAO.getTotalAwardRecordForBoss(dto);
            }

            BuyerWinningRecordConvert convert = new BuyerWinningRecordConvert();
            List<PromotionAwardDTO> awardDTOList = convert.toTarget(list);
            dataGrid.setTotal(count);
            dataGrid.setRows(awardDTOList);
        } catch (Exception e) {
            logger.error("messageId{}:执行方法【getAwardRecordByPromotionId】报错：{}", messageId, e.toString());
            throw new RuntimeException(e);
        }
        return dataGrid;
    }

    @Override
    public int updateLogisticsInfo(PromotionAwardReqDTO dto, String messageId) {
        logger.info("promotionId{}:AwardRecordServiceImpl.updateLogisticsInfo（）方法,入参{}", messageId,
                JSON.toJSONString(dto));

        return awardRecordDAO.updateLogisticsInfo(dto);
    }

    @Override
    public Boolean checkOrder(String orderNo) {
        BuyerWinningRecordDMO dmo = awardRecordDAO.checkOrder(orderNo);
        if(dmo != null){
            return true;
        }
        return false;

    }

    @Override
    public Integer updateOrder(PromotionAwardReqDTO awardReqDTO) {
        return awardRecordDAO.updateOrder(awardReqDTO);
    }

    @Override
    public Integer insertOrder(PromotionAwardReqDTO awardReqDTO) {

        return awardRecordDAO.insertOrder(awardReqDTO);
    }

    @Override
    public int updateOrderLogisticsInfo(PromotionAwardReqDTO dto, String messageId) {
       return  awardRecordDAO.updateOrderLogisticsInfo(dto);
    }
}
