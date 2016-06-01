package cn.ifavor.utils.db;

import java.lang.reflect.Field;

public class DBUtils {

	/**
	 * �� JavaBean ͨ��ע��+��������SQL���
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
		// ��ȡ Clazz ����
		Class<? extends Object> clazz = filter.getClass();
		
		// �жϵ�ǰ�����Ƿ���� @Table ע��
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

	/* ƴ��SQL��� */
	private static String appendSQL(Object filter, StringBuilder sb, Field field) throws Exception {
		Column column = field.getAnnotation(Column.class);
		String columnName = column.value();
		Object columnValue = field.get(filter);
		
		// columnValue != null �ų�������������Ϊnull�ģ�
		//(columnValue instanceof Integer) && ((Integer)columnValue ) == 0)  �ų�����ֵ����Ϊ0��
		if (columnValue != null && !(  (columnValue instanceof Integer) && ((Integer)columnValue ) == 0)){
			if (columnValue instanceof String){
				
				// ��Ժ����ŵ��������� city="a,b"�������ɵ�where������city in ('a','b')
				if (((String) columnValue).contains(",")){
					String[] sArr = ((String) columnValue).split(",");
					String newColumnVal = appendSemicolon(sArr);
					// �Ż����������city=","����split���س���Ϊ0����
					if (newColumnVal != null && newColumnVal.length() > 0){
						sb.append(" and ").append(columnName).append(" in ( ").append(newColumnVal).append(" ) ") ;	
					} else {
						// �Ż��ַ�����''������sql�﷨
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

	/* ���� in ������ֵ */
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
