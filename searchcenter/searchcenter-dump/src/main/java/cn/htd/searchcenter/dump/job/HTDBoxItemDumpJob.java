package cn.htd.searchcenter.dump.job;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import cn.htd.searchcenter.domain.ItemDTO;
import cn.htd.searchcenter.domain.SyncTime;
import cn.htd.searchcenter.dump.job.assembling.DumpConstant;
import cn.htd.searchcenter.dump.job.assembling.ItemDumpJobAssembling;
import cn.htd.searchcenter.service.ItemExportService;
import cn.htd.searchcenter.service.RunFlagService;
import cn.htd.searchcenter.service.SearchPriceService;
import cn.htd.searchcenter.service.SyncTimeService;

public class HTDBoxItemDumpJob {

	private static final Logger LOG = Logger.getLogger(HTDBoxItemDumpJob.class
			.getName());

	@Resource
	private ItemExportService ItemExportService;
	@Resource
	private RunFlagService runFlagService;
	@Resource
	private SearchPriceService searchPriceService;
	@Resource
	private SyncTimeService syncTimeService;
	@Resource
	private ItemDumpJobAssembling itemDumpJobAssembling;

	public void dump() {
		boolean timeFlag = true;
		String runFlag = runFlagService
				.queryJobRunFlagByType(DumpConstant.HTDBOX_ITEM_RUN_TYPE);
		if (null == runFlag || "1".equals(runFlag)) {
			return;
		}
		runFlagService
				.updateRunFlagById(DumpConstant.HTDBOX_ITEM_RUN_TYPE, "1");
		SyncTime syncTime = null;
		Date beginDate = new Date();
		try {
			LOG.info("内部包厢商品任务开始");
			syncTime = syncTimeService
					.getSyncTimeByType(DumpConstant.HTDBOX_ITEM_SYNC_TYPE);
			Date lastSyncTime = syncTime.getLastSyncTime();
			// 获取商品信息
			int countItem = ItemExportService
					.queryHTDBoxItemInfoCountBySyncTime(lastSyncTime);
			if (countItem > 0) {
				int surplus = countItem / DumpConstant.ITEM_IMPORT_COUNT;
				if (surplus > 0) {
					surplus = surplus + 1;
					for (int i = 0; i < surplus; i++) {
						List<ItemDTO> list = ItemExportService
								.queryHTDBoxItemInfoBySyncTime(lastSyncTime, i
										* DumpConstant.ITEM_IMPORT_COUNT,
										DumpConstant.ITEM_IMPORT_COUNT);
						itemDumpJobAssembling
								.solrDataImport(list, lastSyncTime);
					}
				} else {
					List<ItemDTO> list = ItemExportService
							.queryHTDBoxItemInfoBySyncTime(lastSyncTime, 0,
									DumpConstant.ITEM_IMPORT_COUNT);
					itemDumpJobAssembling.solrDataImport(list, lastSyncTime);
				}
			}
		} catch (Exception e) {
			timeFlag = false;
			LOG.error("HTDBoxItem job is error", e);
		} finally {
			runFlagService.updateRunFlagById(DumpConstant.HTDBOX_ITEM_RUN_TYPE,
					"0");
		}

		if (timeFlag) {
			syncTime.setLastSyncTime(beginDate);
			syncTimeService.updateById(syncTime);
		}
		Date endDate = new Date();
		LOG.info("内部包厢商品任务结束");
		LOG.info("本次导入总耗时：" + (endDate.getTime() - beginDate.getTime()) / 1000
				+ "秒");
	}
}
