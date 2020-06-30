package com.imooc.o2o.web.frontend;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.imooc.o2o.dto.UserAwardMapExecution;
import com.imooc.o2o.entity.Award;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.UserAwardMap;
import com.imooc.o2o.enums.UserAwardMapStateEnum;
import com.imooc.o2o.service.AwardService;
import com.imooc.o2o.service.PersonInfoService;
import com.imooc.o2o.service.UserAwardMapService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.ShortNetAddressUtil;

@Controller
@RequestMapping("/frontend")
public class MyAwardController {
	@Autowired
	private UserAwardMapService userAwardMapService;
	@Autowired
	private AwardService awardService;
	@Autowired
	private PersonInfoService personInfoService;

	/**
	 * 根据顾客奖品映射Id获取单条顾客奖品的映射信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getawardbyuserawardid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getAwardbyId(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		/*// 获取前端传递过来的userAwardId
		long userAwardId = HttpServletRequestUtil.getLong(request, "userAwardId");
		// 空值判断
		if (userAwardId > -1) {
			// 根据Id获取顾客奖品的映射信息，进而获取奖品Id
			UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);
			// 根据奖品Id获取奖品信息
			Award award = awardService.getAwardById(userAwardMap.getAward().getAwardId());
			// 将奖品信息和领取状态返回给前端
			modelMap.put("award", award);
			modelMap.put("usedStatus", userAwardMap.getUsedStatus());
			modelMap.put("userAwardMap", userAwardMap);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty awardId");
		}*/
		return modelMap;
	}

	/**
	 * 获取顾客的兑换列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listuserawardmapsbycustomer", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserAwardMapsByCustomer(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 获取分页信息
		/*int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		// 从session中获取用户信息
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		// 空值判断，主要确保用户Id为非空
		if ((pageIndex > -1) && (pageSize > -1) && (user != null) && (user.getUserId() != null)) {
			UserAwardMap userAwardMapCondition = new UserAwardMap();
			userAwardMapCondition.setUser(user);
			long shopId = HttpServletRequestUtil.getLong(request, "shopId");
			if (shopId > -1) {
				// 若店铺id为非空，则将其添加进查询条件，即查询该用户在某个店铺的兑换信息
				Shop shop = new Shop();
				shop.setShopId(shopId);
				userAwardMapCondition.setShop(shop);
			}
			String awardName = HttpServletRequestUtil.getString(request, "awardName");
			if (awardName != null) {
				// 若奖品名为非空，则将其添加进查询条件里进行模糊查询
				Award award = new Award();
				award.setAwardName(awardName);
				userAwardMapCondition.setAward(award);
			}
			// 根据传入的查询条件分页获取用户奖品映射信息
			UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(userAwardMapCondition, pageIndex, pageSize);
			modelMap.put("userAwardMapList", ue.getUserAwardMapList());
			modelMap.put("count", ue.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or userId");
		}*/
		return modelMap;
	}

	/**
	 * 在线兑换礼品
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/adduserawardmap", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addUserAwardMap(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从session中获取用户信息
		/*	PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		// 从前端请求中获取奖品Id
		Long awardId = HttpServletRequestUtil.getLong(request, "awardId");
		// 封装成用户奖品映射对象
		UserAwardMap userAwardMap = compactUserAwardMap4Add(user, awardId);
		// 空值判断
		if (userAwardMap != null) {
			try {
				// 添加兑换信息
				UserAwardMapExecution se = userAwardMapService.addUserAwardMap(userAwardMap);
				if (se.getState() == UserAwardMapStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请选择领取的奖品");
		}*/
		return modelMap;
	}
	
	/**
	 * 封装用户奖品映射实体类
	 * 
	 * @param user
	 * @param awardId
	 * @return
	 */
	private UserAwardMap compactUserAwardMap4Add(PersonInfo user, Long awardId) {
		UserAwardMap userAwardMap = null;
		// 空值判断
		/*if (user != null && user.getUserId() != null && awardId != -1) {
			userAwardMap = new UserAwardMap();
			// 根据用户Id获取用户实体类对象
			PersonInfo personInfo = personInfoService.getPersonInfoById(user.getUserId());
			// 根据奖品Id获取奖品实体类对象
			Award award = awardService.getAwardById(awardId);
			userAwardMap.setUser(personInfo);
			userAwardMap.setAward(award);
			Shop shop = new Shop();
			shop.setShopId(award.getShopId());
			userAwardMap.setShop(shop);
			// 设置积分
			userAwardMap.setPoint(award.getPoint());
			userAwardMap.setCreateTime(new Date());
			// 设置兑换状态为已领取
			userAwardMap.setUsedStatus(1);
		}*/
		return userAwardMap;
	}
}
