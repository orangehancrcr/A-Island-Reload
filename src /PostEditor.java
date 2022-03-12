/* 这是串编辑器, 它是用来进行再次编辑, 锁, 隐藏操作. 
 * @Author: NavelOrange
 * 驱动版本:
 *      mongodb-driver, mongodb-driver-core, bson: 3.6.3;
 *      mongo-java-driver: 3.2.2
 */

// 作者按: 究竟哪个包有用我也忘了
import org.apache.commons.lang.StringUtils;
import java.util.*;
import java.math.*;
import java.lang.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoClient;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.BasicDBObject;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates.*;
/*                                                                                                                                                            
 * 类: 编辑串
 * 需要从外部传入的:
 *      饼干 (透过函数 getActiveCookie(String) 传入 activeCookie);
 *      版面 (透过函数 getActiveBlock(String) 传入 activeBlock);
 */

public class PostEditor
{
	private String activeCookie;
	private String activeBlank;
	private boolean permissionSgn;

	public void getActiveCookie(String input)
	{
		activeCookie = input;
	}

	public void getActiveBlank(String input)
	{
		activeBlank = input;
	}

	//对权限狗串操作的标志
	private void getPermissionSgn()
	{
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("NewTreasureIsland");
		MongoCollection<Document> collection = database.getCollection("admins");
		BasicDBObject filter = new BasicDBObject();
			filter.put("adminName", activeCookie);
			filter.put("permitBlank", activeBlank);
		Document result = collection.find(filter).first();
		if(result == null)
		{
			permissionSgn = false;
		}
		else
		{
			permissionSgn = true;
		}
	}

	//再次编辑. 只有作者本人有此权限
	public void reEdit(int postNum)
	{
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("NewTreasureIsland");
		MongoCollection<Document> collection = database.getCollection("posts");
		BasicDBObject filter = new BasicDBObject();
			filter.put("authorCookie", activeCookie);
			filter.put("postNum", postNum);
		Document result = collection.find(filter).first();
		if(result != null)
		{
			Scanner gets = new Scanner(System.in);
			String input = gets.nextLine();
			collection.updateOne(result, new Document("$set", new Document("text", input)));
			collection.updateOne(result, new Document("$set", new Document("lastTime", new Date())));
		}
		else
		{
			System.out.println("Post does not exist.");
		}
	}

	//隐藏和解隐藏. 只有作者本人有此权限
	public void hide(int postNum)
	{
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("NewTreasureIsland");
		MongoCollection<Document> collection = database.getCollection("posts");
		Document result = collection.find(eq("postNum", postNum)).first();
		if(result != null)
		{
			if(((String)result.get("authorCookie")).compareTo(activeCookie) == 0)
			{
				collection.updateMany(result, new Document("$set", new Document("hideSgn", "enable")));
				System.out.println("Done.");
			}
			else
			{
				System.out.println("Permission denied.");
			}
		}
		else
		{
			System.out.println("Post does not exist.");
		}
	}

	public void visible(int postNum)
	{
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("NewTreasureIsland");
		MongoCollection<Document> collection = database.getCollection("posts");
		Document result = collection.find(eq("postNum", postNum)).first();
		if(result != null)
		{
			if(((String)result.get("authorCookie")).compareTo(activeCookie) == 0)
			{
				collection.updateMany(result, new Document("$set", new Document("hideSgn", "disable")));
				System.out.println("Done.");
			}
			else
			{
				System.out.println("Permission denied.");
			}
		}
		else
		{
			System.out.println("Post does not exist.");
		}
	}

	//删串. 作者和红名有此权限.
	public void remove(int postNum)
	{
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("NewTreasureIsland");
		MongoCollection<Document> collection = database.getCollection("posts");
		Document result = collection.find(eq("postNum",postNum)).first();
		if(result != null)
		{
			if(((String)result.get("authorCookie")).compareTo(activeCookie) == 0 || permissionSgn)
			{
				collection.deleteOne(result);
				System.out.println("Done.");
			}
			else
			{
				System.out.println("Permission denied.");
			}
		}
		else
		{
			System.out.println("Post does not exists.");
		}
	}
	
	//注意, 锁操作若由作者进行, 标记为 enable, 由红名进行则标记为 stable.
	public void lockup(int postNum)
	{
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("NewTreasureIsland");
		MongoCollection<Document> collection = database.getCollection("posts");
		Document result = collection.find(eq("postNum", postNum)).first();
		if(result != null)
		{
			if(((String)result.get("authorCookie")).compareTo(activeCookie) == 0)
			{
				collection.updateMany(result, new Document("$set", new Document("lockSgn", "enable")));
				System.out.println("Done.");
			}
			else if(permissionSgn)
			{
				collection.updateOne(result, new Document("$set", new Document("lockSgn", "stable")));
				System.out.println("Done.");
			}
			else
			{
				System.out.println("Permission denied.");
			}
		}
		else
		{
			System.out.println("Post does not exist.");
		}
	}

	public void unlock(int postNum)
	{
		MongoClient mongoClient = new MongoClient();
		MongoDatabase database = mongoClient.getDatabase("NewTreasureIsland");
		MongoCollection<Document> collection = database.getCollection("posts");
		Document result = collection.find(eq("postNum", postNum)).first();
		if(result != null)
		{
			if(permissionSgn)
			{
				collection.updateOne(result, new Document("$set", new Document("lockSgn", "disable")));
			}
			else if(((String)result.get("lockSgn")).compareTo("enable") == 0&& ((String)result.get("authorCookie")).compareTo(activeCookie) == 0)
			{
				collection.updateOne(result, new Document("$set", new Document("lockSgn", "disable")));
			}
			else
			{
				System.out.println("Permission denied.");
			}
		}
		else
		{
			System.out.println("Post does not exist.");
		}
	}

	/* ----用于测试的主函数---- *
	public static void main(String[] args)
	{
		String testCookie, testBlank;
		Scanner gets = new Scanner(System.in);
		System.out.println("Give me your cookie:");
		testCookie = gets.nextLine();
		System.out.println("Which blank?");
		testBlank = gets.nextLine();
		PostEditor edit = new PostEditor();
		edit.getActiveBlank(testBlank);
		edit.getActiveCookie(testCookie);
		edit.getPermissionSgn();
		edit.reEdit(1);
	}*/
};
