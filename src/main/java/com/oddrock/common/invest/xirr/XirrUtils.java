package com.oddrock.common.invest.xirr;

import java.util.ArrayList;
import java.util.List;

/**
 * XIRR年化收益计算公式
 * @author oddrock
 *
 */
public class XirrUtils {
	public static double computeXirr(List<String> dateStrLst, List<Double> moneyLst) {
		ArrayList<UpbaaDate> list = new ArrayList<UpbaaDate>();
		for(int i=0;i<dateStrLst.size();i++){
			list.add(new UpbaaDate(dateStrLst.get(i),moneyLst.get(i)));
		}
		double xirr = new XirrData(list).getXirr();
		return xirr;
	}
	
	//准备测试数据--日期
	private static List<String> getDates(){
		List<String> list = new ArrayList<String>();
		list.add("2015-12-10");
		list.add("2016-01-15");
		list.add("2016-04-15");
		list.add("2016-07-15");
		list.add("2016-10-15");
		return list;
	}
	
	//准备测试数据--现金流(先负 后正)
	private static List<Double> getCashFlow(){
		List<Double> list = new ArrayList<Double>();
		list.add(-41550000.00);
		list.add(197362.50);
		list.add(498888.54);
		list.add(5118888.54);
		list.add(50682819.17);
		return list;
	}
	
	public static void main(String[] args) {
		System.out.println(computeXirr(getDates(), getCashFlow()));
	}
}
