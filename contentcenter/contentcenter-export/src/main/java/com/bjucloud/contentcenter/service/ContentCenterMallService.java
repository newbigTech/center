package com.bjucloud.contentcenter.service;

import java.util.List;

import com.bjucloud.contentcenter.domain.FloorInfo;
import com.bjucloud.contentcenter.domain.FloorInfoDO;
import com.bjucloud.contentcenter.domain.HeadAd;
import com.bjucloud.contentcenter.domain.HotWord;
import com.bjucloud.contentcenter.domain.LoginPage;
import com.bjucloud.contentcenter.domain.MallChannels;
import com.bjucloud.contentcenter.domain.MallType;
import com.bjucloud.contentcenter.domain.MallTypeSub;
import com.bjucloud.contentcenter.domain.MobileMallCategory;
import com.bjucloud.contentcenter.domain.SiteLogo;
import com.bjucloud.contentcenter.domain.SiteSlogan;
import com.bjucloud.contentcenter.domain.StaticAd;
import com.bjucloud.contentcenter.domain.SubAd;
import com.bjucloud.contentcenter.domain.SubCarouselAd;
import com.bjucloud.contentcenter.domain.TopAd;

import cn.htd.common.ExecuteResult;

public interface ContentCenterMallService {
    /**
     * 查询热词
     *
     * @return
     */
    ExecuteResult<List<HotWord>> selectHotWord();

    /**
     * 取得频道列表
     *
     * @return
     */
    ExecuteResult<List<MallChannels>> selectMallChannels();

    /**
     * 取得顶通广告栏
     *
     * @return
     */
    ExecuteResult<List<TopAd>> selectTopAd();

    /**
     * 取得网站slogan
     *
     * @return
     */
    ExecuteResult<List<SiteSlogan>> selectSiteSlogan();

    /**
     * 取得网站logo
     *
     * @return
     */
    ExecuteResult<List<SiteLogo>> selectSiteLogo();

    /**
     * 取得前台一级类目明细列表
     *
     * @return
     */
    ExecuteResult<List<MallTypeSub>> selectMallTypeSub();

    /**
     * 根据类目ID查询下一级类目明细
     *
     * @param id
     * @return
     */
    ExecuteResult<List<MallTypeSub>> selectMallTypeSubById(Long id);

    /**
     * 取得前台一级类目列表
     *
     * @return
     */
    ExecuteResult<List<MallType>> selectMallType();

    /**
     * 取得轮播广告位列表(包括总部和城市站)
     *
     * @return
     */
    ExecuteResult<List<SubCarouselAd>> selectChannelAd();

    /**
     * 取得登录页logo
     *
     */
    ExecuteResult<LoginPage> selectLoginPage();

    /**
     * 取得静态广告位列表
     */
    ExecuteResult<List<StaticAd>> selectStaticAd();

    /**
     * 取得楼层广告位列表（包括楼层导航、广告详情）
     */
    ExecuteResult<List<FloorInfo>> selectFloorList();

    /**
     * 取得总部轮播广告位列表
     */

    ExecuteResult<List<HeadAd>> selectHeadAd();

    /**
     * 城市站轮播广告位列表
     */
    ExecuteResult<List<SubCarouselAd>> selectSubCarouselAd(String subId);

    /**
     * 取得城市站广告位（广告详情列表）
     */
    ExecuteResult<List<SubAd>> selectSubAd(String subId);

    /**
     * 取得城市站广告位（店铺列表） sort表示列表条数，限制个数
     */
    ExecuteResult<List<SubAd>> selectStoreSubAd(String subId, int sort);

    /**
     * 获取所有楼层信息
     * @return
     */
    ExecuteResult<List<FloorInfoDO>> getAllFloors();

    /**
     * 取得前台一级类目列表(移动商城)
     *
     * @return
     */
    ExecuteResult<List<MobileMallCategory>> queryMobileMallFirstCategoryList();

    /**
     * 根据前台一级类目ID获取二级和三级类目(移动商城)
     *
     * @return
     */
    ExecuteResult<List<MobileMallCategory>> queryMobileMallChildCategoryListByTypeId(Long id);
}
