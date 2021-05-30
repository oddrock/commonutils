package com.oddrock.common.invest.xirr;
 
import java.util.ArrayList;
import java.util.List;
 
 
//计算XIRR
public class Xirr {
	public static void main(String[] args) {
		ArrayList<UpbaaDate> list = new ArrayList<UpbaaDate>();
		List<String> date = getDates();
		List<Double> money = getCashFlow();
		for(int i=0;i<date.size();i++){
			list.add(new UpbaaDate(date.get(i),money.get(i)));
		}
		
		double xirr = new XirrData(list).getXirr();//普通XIRR
		System.out.println(xirr);//输出  0.049039049520590336
		
	}
	
	//准备测试数据--日期
	public static List<String> getDates(){
		List<String> list = new ArrayList<String>();
		list.add("2015-12-10");
		list.add("2016-01-15");
		list.add("2016-04-15");
		list.add("2016-07-15");
		list.add("2016-10-15");
		list.add("2017-01-15");
		list.add("2017-04-15");
		list.add("2017-07-15");
		list.add("2017-10-15");
		list.add("2018-01-15");
		list.add("2018-04-15");
		list.add("2018-07-15");
		list.add("2018-10-15");
		list.add("2019-01-15");
		list.add("2019-04-15");
		list.add("2019-07-15");
		list.add("2019-10-15");
		list.add("2020-01-15");
		list.add("2020-04-15");
		list.add("2020-10-08");
		return list;
	}
	
	//准备测试数据--现金流(先负 后正)
	public static List<Double> getCashFlow(){
		List<Double> list = new ArrayList<Double>();
		list.add(-41550000.00);
		list.add(197362.50);
		list.add(498888.54);
		list.add(5118888.54);
		list.add(5068289.17);
		list.add(392207.50);
		list.add(383681.25);
		list.add(5007944.38);
		list.add(4956125.83);
		list.add(280044.17);
		list.add(273956.25);
		list.add(4897000.21);
		list.add(4843962.50);
		list.add(167880.83);
		list.add(164231.25);
		list.add(4786056.04);
		list.add(4731799.17);
		list.add(55717.50);
		list.add(55111.88);
		list.add(4696590.00);
		return list;
	}
	
}