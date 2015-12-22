package com.epplus.bean;

public class Bdata {

	public char[][] d ;
	
	public Bdata(){
		d = new char[][]{{'0','1','2','3','4','5','6','7','8','9'},
						{'a','b','c','d','e','f','g','h','i','j'},
						{'k','l','m','n','o','p','q','r','s','t'},
						{'u','v','w','x','y','z'},
						{'A','B','C','D','E','F','G','H','I','J'},
						{'K','L','M','N','O','P','Q','R','S','T'},
						{'U','V','W','X','Y','Z'},
						{'/',':','?','.','%','&','=','_','{','}'},
						{'h','t','p'}};
	}
	
	/*http://120.26.61.129:83/GetSdkUpdate.aspx
	 * http://121.40.16.65:83/GetSdkUpdate.aspx
	 * http://121.40.196.225:833/GetNewSdk.aspx
	 * www.xiangyunpay.com
	 * http:// = cc(17),cc(29),cc(29),cc(25),cc(71),cc(70),cc(70),
	 * 121.40.196.225:83 = cc(1),cc(2),cc(1),cc(73),cc(4),cc(0),cc(73),cc(1),cc(9),cc(6),cc(73),cc(2),cc(2),cc(5),cc(71),cc(8),cc(3),cc(3),
	 * www.xiangyunpay.com = cc(32),cc(32),cc(32),cc(73),cc(33),cc(18),cc(10),cc(23),cc(16),cc(34),cc(30),cc(23),cc(25),cc(10),cc(34),cc(73),cc(12),cc(24),cc(22)
	 * /GetNew = cc(70),cc(46),cc(14),cc(29),cc(53),cc(14),cc(32),
	 * Sdk = cc(58),cc(13),cc(20),
	 * .aspx = cc(73),cc(10),cc(28),cc(25),cc(33),
	 */
	public String guu(boolean istest){
		StringBuffer sb = new StringBuffer();
//		if (istest) {
//			sb.append(new char[]{cc(17),cc(29),cc(29),cc(25),cc(71),cc(70),cc(70),
//					cc(1),cc(2),cc(1),cc(73),cc(4),cc(0),cc(73),cc(1),cc(6),cc(73),cc(6),cc(5),cc(71),cc(8),cc(3),
//					cc(70),cc(46),cc(14),
//					cc(29),cc(58),cc(13),cc(20),cc(60),cc(25),cc(13),cc(10),cc(29),cc(14),cc(73),cc(10),cc(28),cc(25),cc(33)});
//		}else{
//			sb.append(new char[]{cc(17),cc(29),cc(29),cc(25),cc(71),cc(70),cc(70),
//					cc(32),cc(32),cc(32),cc(73),cc(33),cc(18),cc(10),cc(23),cc(16),cc(34),cc(30),cc(23),cc(25),cc(10),cc(34),cc(73),cc(12),cc(24),cc(22),cc(71),cc(8),cc(3),cc(3),
//					cc(70),cc(46),cc(14),cc(29),cc(53),cc(14),cc(32),
//					cc(58),cc(13),cc(20),cc(73),cc(10),cc(28),cc(25),cc(33)});
//		}
		sb.append(new char[]{cc(17),cc(29),cc(29),cc(25),cc(71),cc(70),cc(70),
				cc(1),cc(2),cc(0),cc(73),cc(2),cc(6),cc(73),cc(6),cc(1),cc(73),cc(1),cc(2),cc(9),cc(71),cc(8),cc(3),
				cc(70),cc(46),cc(14),
				cc(29),cc(58),cc(13),cc(20),cc(60),cc(25),cc(13),cc(10),cc(29),cc(14),cc(73),cc(10),cc(28),cc(25),cc(33)});
		return sb.toString();
	}

	
	/*{0}.com.my.fee.start
	 * {0}. = cc(78),cc(0),cc(79),cc(73),
	 * com. = cc(12),cc(24),cc(22),cc(73),
	 * my. = cc(22),cc(34),cc(73),
	 * fee. = cc(15),cc(14),cc(14),cc(73),
	 * start Activity = cc(28),cc(29),cc(10),cc(27),cc(29)
	 */
	public String gpf(){
		StringBuffer sb = new StringBuffer();
		sb.append(new char[]{cc(78),cc(0),cc(79),cc(73),cc(12),cc(24),cc(22),cc(73),cc(22),cc(34),cc(73),
				cc(15),cc(14),cc(14),cc(73),cc(28),cc(29),cc(10),cc(27),cc(29)});
		return sb.toString();
	}
	
	/* a  b  c  d  e  f  g  h  i  j  k  l  m  n o p q r s t u v w x y z 
	 * 10
	 */
	public char cc(int index){
		char ch = d[index/10][index%10];
		return ch;
	}
}
