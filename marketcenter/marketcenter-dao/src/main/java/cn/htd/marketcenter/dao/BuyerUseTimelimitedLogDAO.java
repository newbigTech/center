package cn.htd.marketcenter.dao;

import java.util.List;

import cn.htd.common.Pager;
import cn.htd.common.dao.orm.BaseDAO;
import cn.htd.marketcenter.domain.BuyerUseTimelimitedLog;
import org.apache.ibatis.annotations.Param;

public interface BuyerUseTimelimitedLogDAO extends BaseDAO<BuyerUseTimelimitedLog> {

    /**
     * @param condition
     * @param pager
     * @return
     */
    public List<BuyerUseTimelimitedLog> queryNeedReleaseTimelimitedStock4Task(
            @Param("entity") BuyerUseTimelimitedLog condition,
            @Param("page") Pager<BuyerUseTimelimitedLog> pager);

    /**
     * 更新秒杀活动记录的释放库存标记
     *
     * @param useTimelimitedLog
     * @return
     */
    public Integer updateTimelimitedReleaseStockStatus(BuyerUseTimelimitedLog useTimelimitedLog);
}