package com.mobisoft.sms.dao;

import java.nio.channels.SeekableByteChannel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mobisoft.sms.model.Credit;
import com.mobisoft.sms.model.Debit;
import com.mobisoft.sms.model.Product;
import com.mobisoft.sms.model.Route;
import com.mobisoft.sms.model.SmsBalance;
import com.mobisoft.sms.model.SmsDnd;
import com.mobisoft.sms.model.User;
import com.mobisoft.sms.model.UserAuthrization;
import com.mobisoft.sms.model.UserProduct;
import com.mobisoft.sms.utility.Global;
import com.mysql.jdbc.PreparedStatement;

@Repository("smsHelperDao")
public class SmsHelperDaoImpl implements SmsHelperDao{
	
	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	@Qualifier("sessionFactory2")
	SessionFactory sessionFactory2;

	@Override
	public List<Integer> getBalance(int userId, int productId) {
		
		Session session =  sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sqlQuery = "select balance from sms_balance where user_id = "+userId+" and product_id ="+productId;
		Query query = session.createSQLQuery(sqlQuery);
		List list = query.list();
		return list;
	}

	@Override
	public int deductBalanceDeleteUser(int userId, int resellerId, String remark, int deductType,Session session,Transaction tx) {
		int temp = 0;
	   
		User user=(User)session.get(User.class, userId);
		System.out.println("Address "+user.getAddress());
		
		Set<Product> products = new HashSet<Product>();
		//products = user.getUserProduct();
		
		
		
		
        /*System.out.println(products.size());
        
        for (Product prodcutObj : products) {

            List<Integer> balanceList = getBalance(user.getUserId(),prodcutObj.getId());
            Iterator<Integer> iterator = balanceList.iterator();
            while (iterator.hasNext()) {
            	int balance =0;
            	balance = iterator.next();
            	
            	temp = creditBalance(user.getUserId(), prodcutObj.getId(), resellerId, balance, remark, deductType,session,tx);
            	temp = debitBalnce(userId, prodcutObj.getId(), resellerId, balance, remark, deductType,session,tx);
            	
			}
        }*/
        
		return temp;
	}

	@Override
	public int creditBalance(int creditUserId, int productId, int creditByUserId, int balance,String remark,int creditType,Session session,Transaction tx) {
		
		
		Product product = (Product)session.get(Product.class,productId);
		User user=(User)session.get(User.class, creditUserId);
		List<Integer> balanceList = getBalance(user.getUserId(),product.getId());
		int newBalance = balanceList.get(0)+balance;
		Credit credit =new Credit();
		credit.setCredit(balance);
		credit.setCreditBy(creditByUserId);
		credit.setCurrentAmount(newBalance);
		credit.setRemark(remark);
		credit.setCreditType(creditType);
		credit.setPreviousAmouunt(balanceList.get(0));
		credit.setProductId(product);
		credit.setUserId(user);		
		session.saveOrUpdate(credit);
		updateUserBalance(newBalance, user.getUserId(), product.getId(), session, tx);
		
		return newBalance;
	}

	@Override
	public int debitBalnce(int creditUserId, int productId, int debitByUserId, int balance, String remark,int debitType,Session session,Transaction tx) {
		
		int temp =0;
		
		Product product = (Product)session.get(Product.class,productId);
		User resellerUser=(User)session.get(User.class, debitByUserId);
		
		List<Integer> balanceList = getBalance(resellerUser.getUserId(),product.getId());
		int newResellerBalance = balanceList.get(0)-balance;
		
		User user=(User)session.get(User.class, creditUserId);
		Debit debit = new Debit();
		debit.setDebit(balance);
		debit.setCurrentAmount(newResellerBalance);
		debit.setPreviousAmouunt(balanceList.get(0));
		debit.setDebitBy(resellerUser.getUserId());					
		debit.setRemark(remark);			
		debit.setDebitType(debitType);			
		debit.setUserId(user);
		debit.setProductId(product);
		
		session.saveOrUpdate(debit);
		temp = updateUserBalance(newResellerBalance, resellerUser.getUserId(), productId, session, tx);
		
		return temp;
	}

	@Override
	public int updateUserBalance(int newBalance, int userId, int productId,Session session,Transaction tx) {		
		String sqlquery = "update sms_balance set balance = "+newBalance+" where user_id= "+userId+" and product_id="+productId;
		Query query = session.createSQLQuery(sqlquery);
		int updateSmsBalanceResult = query.executeUpdate();
		return updateSmsBalanceResult;
	}

	@Override
	public int creditOnUser(int newBalance, int userId, int productId, int creditInUser, int creditByReseller,
			Session session, Transaction tx) {
	/*	int temp =0;
		Product product = (Product)session.get(Product.class,productId);
		User user=(User)session.get(User.class, resellerId);
		List<Integer> balanceList = getBalance(user.getUserId(),product.getId());
		int oldBalance = balanceList.get(0)+newBalance;
		Credit credit =new Credit();
		credit.setCredit(balance);
		credit.setCreditBy(resellerId);
		credit.setCurrentAmount(newBalance);
		credit.setRemark(remark);
		credit.setCreditType(creditType);
		credit.setPreviousAmouunt(balanceList.get(0));
		credit.setProductId(product);
		credit.setUserId(user);		
		session.saveOrUpdate(credit);
		updateUserBalance(newBalance, user.getUserId(), product.getId(), session, tx);
		
		return newBalance;*/
		return 0;
	}

	@Override
	public  List<SmsBalance> findProdcut(int userId, int prodcutId) {
		Session session= sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		User user =(User)session.load(User.class, userId);
		Product product =(Product)session.load(Product.class, prodcutId);
		Criteria criteria=session.createCriteria(SmsBalance.class);
		criteria.add(Restrictions.eq("userId", user)).add(Restrictions.eq("productId", product)).setFirstResult(0);
		 List<SmsBalance> list = criteria.list();				
		return list;
	}

	@Override
	public int messageCount(int messageType, int messageLenght) {
		int messageCount=0;
        
        if(messageType == 1)
        {
            int part1Count = 160;
            int part2Count = 146;
            int part3Count = 153;
           
           
            if(messageLenght <= part1Count)
            {
                messageCount =1;
            }
            else if(messageLenght <= (part1Count + part2Count))
            {
                messageCount = 2;
            }
            else if(messageLenght > (part1Count+part2Count)){
               
               
                double  count = Math.ceil(( messageLenght - part1Count - part2Count) / part3Count);
                System.out.println(count);
                messageCount = (int)(count + 3);
                
            }
           
            System.out.println("Text Message "+messageCount);
        }
        else
        {
            int part1Count = 70;
            int part2Count = 64;
            int part3Count = 67;
            
            
            if(messageLenght <= part1Count)
            {
                messageCount =1;
            }
            else if(messageLenght <= (part1Count + part2Count))
            {
                messageCount = 2;
            }
            else if(messageLenght > (part1Count+part2Count)){
               
               
                double  count = Math.ceil(( messageLenght - part1Count - part2Count) / part3Count);
                System.out.println(count);
                messageCount = (int)(count + 3);
                
            }
           
            System.out.println("Unicode "+ messageCount);
        }
        return messageCount;
	}
	@Override
	public List<SmsDnd> filterDndNumber() {
		Session session = sessionFactory2.openSession();
		Transaction tx = session.beginTransaction();
		List<SmsDnd> list = session.createCriteria(SmsDnd.class).list();
		return list;
	}
	@Override
	public List<UserProduct> getRouteDetails(int userId, int productId) {
	
		Session session= sessionFactory.openSession();
		User user=(User)session.get(User.class, userId);
		Product product= (Product)session.get(Product.class, productId);
		Criteria criteria= session.createCriteria(UserProduct.class);
		criteria.add(Restrictions.eq("userId", user))
		 		.add(Restrictions.eq("productId", product));
		List<UserProduct> userProductsList = criteria.list();		
		return userProductsList;
	}
	@Override
	public List<String> getGroupContact(String groupId, int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		String sqlQuery = "SELECT mobile FROM contact WHERE group_id in("+groupId+") and user_id ="+userId;
		Query query = session.createSQLQuery(sqlQuery);
		List<String> list = query.list();
		return list;
		
	}
	@Override
	public List<UserAuthrization> getUserAuthrizationCheck(int userId) {
		Session session = sessionFactory.openSession();
		Transaction tx= session.beginTransaction();
		User user =(User)session.get(User.class, userId);
		Criteria criteria = session.createCriteria(UserAuthrization.class);
		criteria.add(Restrictions.eq("userId",user));
		List<UserAuthrization> list = criteria.list();
		return list;		
	}
	@Override
	public List<Object> mobileNumber(final String mobileNumber) {
		
		Session session = sessionFactory.openSession();
		Transaction tx =  session.beginTransaction();
		final List<Object> commonListData = new ArrayList<Object>();
		try {
			session.doWork(new Work() {				
			       @Override
			       public void execute(Connection conn) throws SQLException {

				          PreparedStatement pstmtDlrStatus = null;
				          List<String> mobileList = Arrays.asList(mobileNumber.split("\\s*,\\s*"));
				          List<String> listMatchDndData = new ArrayList<String>();
				          
				          try{
				           String sqlInsertDlrStatus = "INSERT INTO temp_dnd(mobile) VALUES (?)";
				           pstmtDlrStatus = (PreparedStatement) conn.prepareStatement(sqlInsertDlrStatus);
				           int i=0;
				           for(String mobile : mobileList){	
				        	   
				        	   if(mobile.length() > 5)
				        	   {
				        		   if(mobile.length() == 12)
					        	 	{
					        	 		mobile = mobile.substring(2);				        	 		
					        	 	}				        	
					        	   pstmtDlrStatus.setString(1, mobile);
					               pstmtDlrStatus.addBatch();
				        	   }
				        	   
				           }
				           conn.setAutoCommit(false);
				           pstmtDlrStatus.executeBatch();
				           
				         //conn.setAutoCommit(true);
				           String selectMobileNumberQuery ="select  Distinct t.mobile from  mobi_sms.temp_dnd t  inner join  sms_dnd.mobi_dnd d on t.mobile = d.mobile_no";
				           System.out.println(selectMobileNumberQuery);
				           Statement statement = conn.createStatement();
				           ResultSet rs = statement.executeQuery(selectMobileNumberQuery);
				           while(rs.next())
				           {
				        	   System.out.println(rs.getString("mobile"));
				        	   String newMobile = rs.getString("mobile").toString();
				        	   System.out.println("New Mobile"+newMobile);
				        	   StringBuilder s =  new StringBuilder(newMobile);
				        	   newMobile = s.insert(0,"91").toString();
				        	   listMatchDndData.add(newMobile);
				           }
				           ///Trancute table temp table
				           String trancuteTable ="TRUNCATE TABLE temp_dnd";
				           System.out.println(selectMobileNumberQuery);
				           Statement statementTrancute = conn.createStatement();
				           ResultSet resultSetTrancute = statementTrancute.executeQuery(trancuteTable);
				           
				           conn.commit();
				        //  System.out.println("New Mobile list Size33:"+listMatchDndData);
				        //  System.out.println("old Mobile list Size33:"+mobileList);
				           List<String> newFilterMobileList = new ArrayList<>();
				           for(int j=0; j<mobileList.size(); j++)
				           {
				        	   if(mobileList.get(j).length() != 12)
				        	   {
				        		   if(mobileList.get(j).length() != 0)
				        		   {
				        			   StringBuilder s =  new StringBuilder(mobileList.get(j));
						        	   String add91Mobile = s.insert(0,"91").toString();
						        	   newFilterMobileList.add(add91Mobile);
				        		   }
				        	   }
				        	   else
				        	   {
				        		   if(mobileList.get(j).length() != 0)
				        		   {
				        			   newFilterMobileList.add(mobileList.get(j));
				        		   }				        		  
				        	   } 
				           }
				           newFilterMobileList.removeAll(listMatchDndData);
				           commonListData.add(0, newFilterMobileList);
				           commonListData.add(1, listMatchDndData.size());
				           System.out.println("old Mobile list Size:"+mobileList.size());
				           System.out.println("New Mobile list Size: "+newFilterMobileList);
				         } 
				         finally{
				        	 pstmtDlrStatus .close(); 
				         }
			     }

			});
		} catch (Exception e) {
			tx.rollback();
		}

		return commonListData;
	}

}
