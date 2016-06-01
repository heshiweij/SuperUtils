package cn.ifavor.utils.db;

import java.lang.reflect.Field;

public class DBUtils {

	/**
	 * 将 JavaBean 通过注解+反射生成SQL语句
	 * @param filter
	 * @return
	 */
	public static String query(Object filter)  {
		try {
			return queryByFlilter(filter);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static String queryByFlilter(Object filter) throws Exception {
		// 获取 Clazz 对象
		Class<? extends Object> clazz = filter.getClass();
		
		// 判断当前的类是否存在 @Table 注解
		boolean isExists = clazz.isAnnotationPresent(Table.class);
		if (isExists){
			StringBuilder sb = new StringBuilder("select * from ");
			Table table = (Table) clazz.getAnnotation(Table.class);
			sb.append(table.value());
			
			sb.append(" where 1=1 ");
			
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++){
				Field field = fields[i];
				field.setAccessible(true);
				boolean isColumnExists = field.isAnnotationPresent(Column.class);
				if (isColumnExists){
					appendSQL(filter, sb, field);
				}
			}
			return sb.toString();
		}
		
		return null;
	}

	/* 拼接SQL语句 */
	private static String appendSQL(Object filter, StringBuilder sb, Field field) throws Exception {
		Column column = field.getAnnotation(Column.class);
		String columnName = column.value();
		Object columnValue = field.get(filter);
		
		// columnValue != null 排除所有引用类型为null的，
		//(columnValue instanceof Integer) && ((Integer)columnValue ) == 0)  排除所有值类型为0的
		if (columnValue != null && !(  (columnValue instanceof Integer) && ((Integer)columnValue ) == 0)){
			if (columnValue instanceof String){
				
				// 针对含逗号单独处理，如 city="a,b"，则生成的where条件：city in ('a','b')
				if (((String) columnValue).contains(",")){
					String[] sArr = ((String) columnValue).split(",");
					String newColumnVal = appendSemicolon(sArr);
					// 优化特殊情况：city=","，则split返回长度为0数组
					if (newColumnVal != null && newColumnVal.length() > 0){
						sb.append(" and ").append(columnName).append(" in ( ").append(newColumnVal).append(" ) ") ;	
					} else {
						// 优化字符串加''，符合sql语法
						sb.append(" and ").append(columnName).append("=").append( "'"+columnValue + "'");	
					}
					
				} else {
					sb.append(" and ").append(columnName).append("=").append( "'"+columnValue + "'");	
				}
				
			} else {
				sb.append(" and ").append(columnName).append("=").append(columnValue);
			}
				
		}
		return sb.toString();
	}

	/* 处理 in 条件的值 */
	private static String appendSemicolon(String[] sArr) {
		if (sArr != null && sArr.length > 0){
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < sArr.length; i++){
				sb.append("'" + sArr[i] + "'").append(",");
			}
			sb.delete(sb.length() - 1, sb.length());
			return sb.toString();
		}
		
		return "";
	}
}
