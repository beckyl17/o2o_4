package com.imooc.o2o.web.shopadmin;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ShopAuthMapExecution;
import com.imooc.o2o.dto.UserAccessToken;
import com.imooc.o2o.dto.UserAwardMapExecution;
import com.imooc.o2o.entity.Award;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopAuthMap;
import com.imooc.o2o.entity.UserAwardMap;
import com.imooc.o2o.enums.UserAwardMapStateEnum;
import com.imooc.o2o.service.PersonInfoService;
import com.imooc.o2o.service.ShopAuthMapService;
import com.imooc.o2o.service.UserAwardMapService;
import com.imooc.o2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shopadmin")
public class UserAwardManagementController {
	@Autowired
	private UserAwardMapService userAwardMapService;
	@Autowired
	private PersonInfoService personInfoService;
	@Autowired
	private ShopAuthMapService shopAuthMapService;

	/**
	 * 列出某个店铺的用户奖品领取情况列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listuserawardmapsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserAwardMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从session里获取店铺信息
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		// 获取分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		// 空值判断
		if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
			UserAwardMap userAwardMap = new UserAwardMap();
			userAwardMap.setShop(currentShop);
			// 从请求中获取奖品名
			String awardName = HttpServletRequestUtil.getString(request, "awardName");
			if (awardName != null) {
				// 如果需要按照奖品名称搜索，则添加搜索条件
				Award award = new Award();
				award.setAwardName(awardName);
				userAwardMap.setAward(award);
			}
			// 分页返回结果
			UserAwardMapExecution ue = userAwardMapService.listReceivedUserAwardMap(userAwardMap, pageIndex, pageSize);
			modelMap.put("userAwardMapList", ue.getUserAwardMapList());
			modelMap.put("count", ue.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}


	/**
	 * 封装用户奖品映射实体类，以供扫码使用，主要将其领取状态变为已领取
	 * 
	 * @param customerId
	 * @param userAwardId
	 * @return
	 */
	private UserAwardMap compactUserAwardMap4Exchange(Long customerId, Long userAwardId, PersonInfo operator) {
		UserAwardMap userAwardMap = null;
		// 空值判断
		if (customerId != null && userAwardId != null && operator != null) {
			// 获取原有userAwardMap信息
			userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);
			userAwardMap.setUsedStatus(1);
			PersonInfo customer = new PersonInfo();
			customer.setUserId(customerId);
			userAwardMap.setUser(customer);
			userAwardMap.setOperator(operator);
		}
		return userAwardMap;
	}

	/**
	 * 检查员工是否具有授权权限
	 * 
	 * @param userId
	 * @param userAwardMap
	 * @return
	 */
	private boolean checkShopAuth(long userId, UserAwardMap userAwardMap) {
		// 取出该店铺所有的授权信息
		ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService
				.listShopAuthMapByShopId(userAwardMap.getShop().getShopId(), 1, 1000);
		// 逐条遍历，看看扫描二维码的员工是否具有扫码权限
		for (ShopAuthMap shopAuthMap : shopAuthMapExecution.getShopAuthMapList()) {
			if (shopAuthMap.getEmployee().getUserId() == userId && shopAuthMap.getEnableStatus() == 1) {
				return true;
			}
		}
		return false;
	}
}
