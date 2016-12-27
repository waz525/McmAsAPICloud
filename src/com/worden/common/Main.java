package com.worden.common;


public class Main {


	public static void main(String[] args) {
		
		/*
		MysqlUtils mu = new MysqlUtils("192.168.159.3:3306","root","123456","MobileOA") ;
		
		
		Row[] rows = mu.Query("user", "id,username,password,createdAt" ) ;
		
		CommonUtil.PrintInfo("rows count : "+rows.length);
		for( int i = 0 ; i<rows.length ; i++) {
			Cell[] cellList = rows[0].getCellList() ;
			for( int j = 0 ; j<cellList.length ; j++) {
				Cell c = cellList[j] ;
				System.out.println(i+" -- "+j+" : "+c.getColumnName()+ " | " + c.getValue() +" | "+c.getType());
			}
		}
		*/
		
		//CommonUtil.PrintInfo(mu.getTableDesc("user1")[0]) ;
		
		//CommonUtil.PrintInfo(CommonUtil.GetRandomCodes(24));
		/*
		boolean exist = true ;
		try {
			 mu.getCount("ttt") ;
		} catch( Exception e ) {
			exist = false ;
		}
		
		CommonUtil.PrintInfo("exist: " + exist) ;
		
		
		String str = "{\"nextApproval_id\":\"A4006\",\"password\":\"admin123\",\"mobile\":{\"like\": \"15\"},\"username\":\"X4044\"}";
		
		Map<String,Object> map = GsonUtil.JsontoMap(str);
		
		CommonUtil.PrintInfo("Map.username = "+ map.get("username"));
		*/
		/*
		String str = JWTUtil.createToken("5797f27a102649c5a1d8c17d") ;
		
		CommonUtil.PrintInfo("JWTUtil.createToken = "+str);
		CommonUtil.PrintInfo("JWTUtil.validToken = "+JWTUtil.validToken(str).get("state"));
		*/
		test("aaa" ) ;
		test("aaa", "bbb", "ccc");  
		
	}
	
	public static void test(String... args) {  
        
        for (String s : args) {  
            System.out.print(" ==> "+s );  
        }
        System.out.println();
    }  
}
