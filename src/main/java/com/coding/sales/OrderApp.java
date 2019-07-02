package com.coding.sales;

import com.coding.sales.bean.GoodsInfo;
import com.coding.sales.bean.MemberInfo;
import com.coding.sales.input.OrderCommand;
import com.coding.sales.input.OrderItemCommand;
import com.coding.sales.input.PaymentCommand;
import com.coding.sales.output.DiscountItemRepresentation;
import com.coding.sales.output.OrderItemRepresentation;
import com.coding.sales.output.OrderRepresentation;
import com.coding.sales.output.PaymentRepresentation;

import java.math.BigDecimal;
import java.text.Bidi;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 销售系统的主入口
 * 用于打印销售凭证
 */
public class OrderApp {

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("参数不正确。参数1为销售订单的JSON文件名，参数2为待打印销售凭证的文本文件名.");
        }

        String jsonFileName = args[0];
        String txtFileName = args[1];

        String orderCommand = FileUtils.readFromFile(jsonFileName);
        OrderApp app = new OrderApp();
        try {
            String result = app.checkout(orderCommand);
            FileUtils.writeToFile(result, txtFileName);
        } catch (Exception ee) {
            FileUtils.writeToFile(ee.getMessage(), txtFileName);
        }
    }

    public String checkout(String orderCommand) throws Exception {
        OrderCommand command = OrderCommand.from(orderCommand);
        OrderRepresentation result = checkout(command);

        return result.toString();
//        return "";
    }

    OrderRepresentation checkout(OrderCommand command) throws Exception {
        String date = command.getCreateTime();
        MemberInfo memberInfo = getMemberInfo(command.getMemberId());
        if (memberInfo == null) {
            throw new Exception("客户Id不存在，请检查");
        }
        List<OrderItemCommand> items = command.getItems();
        List<String> discounts = command.getDiscounts();
        List<PaymentCommand> paymentCommands = command.getPayments();

        List<DiscountItemRepresentation> discountItemRepresentations = new ArrayList<DiscountItemRepresentation>();
        List<OrderItemRepresentation> orderItemRepresentations = new ArrayList<OrderItemRepresentation>();
        List<String> payInfo = new ArrayList<String>();

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItemCommand orderItemCommand : items) {
            String product = orderItemCommand.getProduct();
            BigDecimal amount = orderItemCommand.getAmount();
            GoodsInfo currentGoods = null;
            for (GoodsInfo goodsInfo : goodsList) {
                if (product.equals(goodsInfo.getcCode())) {
                    currentGoods = goodsInfo;
                    break;
                }
            }
            if (currentGoods == null) {
                throw new Exception("商品 product 不存在");
            }

            BigDecimal allAmountForEvery = currentGoods.getbPirce().multiply(amount);
            //参与满减
            BigDecimal allDiscountForEvery = BigDecimal.ZERO;
            if (currentGoods.isbDisCountForEvery() && currentGoods.getListDisCountForEvery() != null) {
                BigDecimal tempAmount = allAmountForEvery;
                for (HashMap discountForEvery : currentGoods.getListDisCountForEvery()) {
                    BigDecimal bPrice = (BigDecimal) discountForEvery.get("bPrice");
                    BigDecimal bDiscount = (BigDecimal) discountForEvery.get("bDiscount");

//                    BigDecimal tempAmount = allAmountForEvery;
//                    if (tempAmount.compareTo(bPrice) > 0){
//                        allDiscountForEvery = allDiscountForEvery.add(bDiscount);
//                        break;
//                    }
                    //不可多次满减
                    for (;tempAmount.compareTo(bPrice) > 0;
                         tempAmount = tempAmount.subtract(bPrice)) {
                        allDiscountForEvery = allDiscountForEvery.add(bDiscount);
                    }
                }
            }
            BigDecimal allDiscountPrice = BigDecimal.ZERO;
            if (currentGoods.getbDiscount().compareTo(new BigDecimal("1")) == 0) {

            } else if (currentGoods.getbDiscount().compareTo(new BigDecimal("0.9")) == 0) {
                if (discounts != null && discounts.size() > 0) {
                    for (String s : discounts) {
                        if (s != null && s.contains("9折券")) {
                            allDiscountPrice = allAmountForEvery.multiply(new BigDecimal("0.1"));
                            boolean flag = false;
                            for (String pay : payInfo) {
                                if ("9折券".equals(pay)) {
                                    flag = true;
                                }
                            }
                            if (flag != true) {
                                payInfo.add("9折券");
                            }
                            break;
                        }
                    }
                }
            } else if (currentGoods.getbDiscount().compareTo(new BigDecimal("0.95")) == 0) {
                if (discounts != null && discounts.size() > 0) {
                    for (String s : discounts) {
                        if (s != null && s.contains("95折券")) {
                            allDiscountPrice = allAmountForEvery.multiply(new BigDecimal("0.05"));
                            boolean flag = false;
                            for (String pay : payInfo) {
                                if ("95折券".equals(pay)) {
                                    flag = true;
                                }
                            }
                            if (flag != true) {
                                payInfo.add("95折券");
                            }
                            break;
                        }
                    }
                }
            }
            BigDecimal discountResult = allDiscountForEvery.compareTo(allDiscountPrice) > 0 ? allDiscountForEvery : allDiscountPrice;
            totalPrice = totalPrice.add(allAmountForEvery.subtract(discountResult));
            if (discountResult.compareTo(BigDecimal.ZERO) != 0) {
                discountItemRepresentations.add(new DiscountItemRepresentation(currentGoods.getcCode(), currentGoods.getcName(), discountResult));
            }
            orderItemRepresentations.add(new OrderItemRepresentation(currentGoods.getcCode(), currentGoods.getcName(),
                    currentGoods.getbPirce(), amount, allAmountForEvery));
        }
        String cLevel = memberInfo.getcLevel();
        long rank = memberInfo.getiRank();
        String levelResult = cLevel;
        long jifen = 0;
        if ("普卡".equals(cLevel)) {
            jifen = totalPrice.intValue();
            rank = memberInfo.getiRank() + jifen;
        } else if ("金卡".equals(cLevel)) {
            jifen = totalPrice.multiply(new BigDecimal("1.5")).intValue();
            rank = memberInfo.getiRank() + jifen;
        } else if ("白金卡".equals(cLevel)) {
            jifen = totalPrice.multiply(new BigDecimal("1.8")).intValue();
            rank = memberInfo.getiRank() + jifen;
        } else if ("钻石卡".equals(cLevel)) {
            jifen = totalPrice.multiply(new BigDecimal("2")).intValue();
            rank = memberInfo.getiRank() + jifen;
        }
        if (rank < 10000) {
            levelResult = "普卡";
        } else if (rank >= 10000 && rank < 50000) {
            levelResult = "金卡";
        } else if (rank >= 50000 & rank < 100000) {
            levelResult = "白金卡";
        } else if (rank >= 100000) {
            levelResult = "钻石卡";
        }
        BigDecimal subTotal = BigDecimal.ZERO;
        for (OrderItemRepresentation orderItemRepresentation : orderItemRepresentations) {
            subTotal = subTotal.add(orderItemRepresentation.getSubTotal());
        }
        BigDecimal discountTotal = BigDecimal.ZERO;
        for (DiscountItemRepresentation discountItemRepresentation : discountItemRepresentations) {
            discountTotal = discountTotal.add(discountItemRepresentation.getDiscount());
        }
        List<PaymentRepresentation> paymentRepresentations = new ArrayList<PaymentRepresentation>();
        for (PaymentCommand paymentCommand : paymentCommands) {
            paymentRepresentations.add(new PaymentRepresentation(paymentCommand.getType(), paymentCommand.getAmount()));
        }
        OrderRepresentation result = new OrderRepresentation(command.getOrderId(), getdCreateTime(date),
                memberInfo.getcCode(), memberInfo.getcName(), memberInfo.getcLevel(),
                levelResult, (int) jifen, (int) rank, orderItemRepresentations, subTotal, discountItemRepresentations, discountTotal
                , totalPrice, paymentRepresentations, payInfo
        );
        return result;
    }

    private Date getdCreateTime(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(date);
    }

    private static List<MemberInfo> memberList;

    static {
        memberList = new ArrayList<MemberInfo>();
        memberList.add(new MemberInfo("马丁", "普卡", "6236609999", 9860));
        memberList.add(new MemberInfo("王立", "金卡", "6630009999", 48860));
        memberList.add(new MemberInfo("李想", "白金卡", "8230009999", 98860));
        memberList.add(new MemberInfo("张三", "钻石卡", "9230009999", 198860));
    }

    private static List<GoodsInfo> goodsList;

    static {

        goodsList = new ArrayList<GoodsInfo>();
        goodsList.add(new GoodsInfo("世园会五十国钱币册", "001001", "册", new BigDecimal("998.00"),
                new BigDecimal("1"), false, null));
        goodsList.add(new GoodsInfo("2019北京世园会纪念银章大全40g", "001002", "盒", new BigDecimal("1380.00"),
                new BigDecimal("0.9"), false, null));
        goodsList.add(new GoodsInfo("财进宝", "003002", "条", new BigDecimal("1580.00"),
                new BigDecimal("0.95"), false, null));
        //第3件半价，满3送1
        List<HashMap> disCountForEvery3002 = new ArrayList<HashMap>();
        HashMap<String, BigDecimal> hashMap = new HashMap<String, BigDecimal>();
        hashMap.put("bPrice", new BigDecimal("2940.00"));
        hashMap.put("bDiscount", new BigDecimal("490.00"));
        disCountForEvery3002.add(hashMap);
        HashMap<String, BigDecimal> hashMap2 = new HashMap<String, BigDecimal>();
        hashMap2.put("bPrice", new BigDecimal("3920.00"));
        hashMap2.put("bDiscount", new BigDecimal("980.00"));
        disCountForEvery3002.add(hashMap2);
        goodsList.add(new GoodsInfo("水晶之恋", "003002", "条", new BigDecimal("980.00"),
                new BigDecimal("1"), true, disCountForEvery3002));

        //每满2000减30，每满1000减10
        List<HashMap> disCountForEvery2002 = new ArrayList<HashMap>();
        HashMap<String, BigDecimal> hashMap3 = new HashMap<String, BigDecimal>();
        hashMap3.put("bPrice", new BigDecimal("2000.00"));
        hashMap3.put("bDiscount", new BigDecimal("30.00"));
        disCountForEvery2002.add(hashMap3);
        HashMap<String, BigDecimal> hashMap4 = new HashMap<String, BigDecimal>();
        hashMap4.put("bPrice", new BigDecimal("1000.00"));
        hashMap4.put("bDiscount", new BigDecimal("10.00"));
        disCountForEvery2002.add(hashMap4);
        goodsList.add(new GoodsInfo("中国经典钱币套装", "002002", "套", new BigDecimal("998.00"),
                new BigDecimal("1"), true, disCountForEvery2002));


        //第3件半价，满3送1
        List<HashMap> disCountForEvery2001 = new ArrayList<HashMap>();
        HashMap<String, BigDecimal> hashMap5 = new HashMap<String, BigDecimal>();
        hashMap5.put("bPrice", new BigDecimal("3240.00"));
        hashMap5.put("bDiscount", new BigDecimal("540.00"));
        disCountForEvery2001.add(hashMap5);
        HashMap<String, BigDecimal> hashMap6 = new HashMap<String, BigDecimal>();
        hashMap6.put("bPrice", new BigDecimal("4320.00"));
        hashMap6.put("bDiscount", new BigDecimal("1080.00"));
        disCountForEvery2001.add(hashMap6);
        goodsList.add(new GoodsInfo("守扩之羽比翼双飞4.8g", "002001", "条", new BigDecimal("1080.00"),
                new BigDecimal("0.95"), true, disCountForEvery2001));

        //参与满减：每满3000元减350, 每满2000减30，每满1000减10
        List<HashMap> disCountForEvery2003 = new ArrayList<HashMap>();
        HashMap<String, BigDecimal> hashMap7 = new HashMap<String, BigDecimal>();
        hashMap7.put("bPrice", new BigDecimal("3000.00"));
        hashMap7.put("bDiscount", new BigDecimal("350.00"));
        disCountForEvery2003.add(hashMap7);
        HashMap<String, BigDecimal> hashMap8 = new HashMap<String, BigDecimal>();
        hashMap8.put("bPrice", new BigDecimal("2000.00"));
        hashMap8.put("bDiscount", new BigDecimal("30.00"));
        disCountForEvery2003.add(hashMap8);
        HashMap<String, BigDecimal> hashMap9 = new HashMap<String, BigDecimal>();
        hashMap9.put("bPrice", new BigDecimal("1000.00"));
        hashMap9.put("bDiscount", new BigDecimal("10.00"));
        disCountForEvery2003.add(hashMap9);
        goodsList.add(new GoodsInfo("中国银象棋12g", "002003", "套", new BigDecimal("698.00"),
                new BigDecimal("0.9"), true, disCountForEvery2003));
    }

    MemberInfo getMemberInfo(String memberId) {
        if (memberId == null)
            return null;
        for (MemberInfo memberInfo : memberList) {
            if (memberId.equals(memberInfo.getcCode())) {
                return memberInfo;
            }
        }
        return null;
    }
}
