package com.epplus.view;
/**
 * 是否显示
 * @author zgt
 *
 */
public interface ShowFlag {
   /**
    * 支付宝
    */
   String alipay = "alipay";
   /**
    *银联
    */
   String unionpay = "unionpay";
   /**
    * 微信
    */
   String  wechatpay = "wechatpay";
   /**
    * 百度
    */
   String  baidupay  = "baidupay";
   /**
    * 短信
    */
   String smspay = "smspay";
   
   /**
    * 微信wap
    */
   String wxWapPay = "wxwap";
   
   /**
    * 显示资费
    */
   String productInfo = "productInfo";
   
   /**
    * 网游 远程订单 id
    */
   String webOrderid = "webOrderid";
   
   /**
    * 游戏类型
    */
   String gameType = "0";
   
   /**
    * 单机
    */
   String danji = "0";
   /**
    * 网游
    */
   String wangyou="1";
   
}
