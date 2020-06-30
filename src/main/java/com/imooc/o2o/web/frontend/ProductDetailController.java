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
import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.UserProductMap;
import com.imooc.o2o.enums.UserProductMapStateEnum;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.service.UserProductMapService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.ShortNetAddressUtil;

@Controller
@RequestMapping("/frontend")
public class ProductDetailController {
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserProductMapService userProductMapService;

	/**
	 * 根据商品Id获取商品详情
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listproductdetailpageinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listProductDetailPageInfo(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 获取前台传递过来的productId
		long productId = HttpServletRequestUtil.getLong(request, "productId");
		Product product = null;
		// 空值判断
		if (productId != -1) {
			// 根据productId获取商品信息，包含商品详情图列表
			product = productService.getProductById(productId);
			//2.0新增
			PersonInfo user = (PersonInfo) request.getSession().getAttribute(
					"user");
			if (user == null) {
				modelMap.put("needQRCode", false);
			} else {
				modelMap.put("needQRCode", true);
			}
			modelMap.put("product", product);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty productId");
		}
		return modelMap;
	}

	
	
	@RequestMapping(value = "/adduserproductmap", method = RequestMethod.GET)
	private String addUserProductMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取用户登录信息
		PersonInfo auth = (PersonInfo) request.getSession().getAttribute("user");
		Long productId = HttpServletRequestUtil.getLong(request, "productId");
		if (auth != null) {
			UserProductMap userProductMap = compactUserProductMap4Add(productId, auth);
			// 空值校验
			if (userProductMap != null) {
				try {
					// 添加消费记录
					UserProductMapExecution se = userProductMapService.addUserProductMap(userProductMap);
					if (se.getState() == UserProductMapStateEnum.SUCCESS.getState()) {
						return "shop/operationsuccess";
					}
				} catch (RuntimeException e) {
					return "shop/operationfail";
				}

			}
		}
		return "shop/operationfail";
	}
	
	/**
	 * 根据传入的customerId, productId以及操作员信息组建用户消费记录
	 * 
	 * @param customerId
	 * @param operator
	 * @return
	 */
	private UserProductMap compactUserProductMap4Add(Long productId, PersonInfo customer) {
		UserProductMap userProductMap = null;
		if (productId != null) {
			userProductMap = new UserProductMap();
			// 主要为了获取商品积分
			Product product = productService.getProductById(productId);
			userProductMap.setProduct(product);
			userProductMap.setShop(product.getShop());
			userProductMap.setUser(customer);
			userProductMap.setPoint(product.getPoint());
			userProductMap.setOperator(customer);
			userProductMap.setCreateTime(new Date());
		}
		return userProductMap;
	}
}
