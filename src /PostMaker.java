/* 这是用来写新串的类.
 * @Author: NavelOrange
 * 驱动版本:
 *      mongodb-driver, mongodb-driver-core, bson: 3.6.3;
 *      mongo-java-driver: 3.2.2
 */

import java.util.*;
import java.math.*;
import java.lang.*;
import org.bson.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates.*;

/*
 * 类: 写新串
 * 需要从外部传入的:
 *      串文 (透过函数 getPostText(String) 传入 postText);
 *      饼干 (透过函数 getActiveCookie(String) 传入 activeCookie);
 *      版面 (透过函数 getActiveBlock(String) 传入 activeBlock);
 */

public class PostMaker
{
        private String postText;
        private String activeCookie;                                                                                                                          
        private String activeBlock;

        private void savePost()
        {
                System.out.println("Connect to database...");
                MongoClient mongoClient = new MongoClient(); //连接到本地数据库.
                MongoDatabase database = mongoClient.getDatabase("NewTreasureIsland");
                System.out.println("Done.");
                MongoCollection<Document> collection = database.getCollection("posts");
                Document result = collection.find().sort(Sorts.descending("_id")).first(); //获取最新串号并 +1
                int lastPostNum = (int)result.get("postNum");
                Document document = new Document("postNum", lastPostNum + 1) //串号
                        .append("text", postText) //串文
                        .append("sendTime", new Date()) //发送时间
                        .append("lastTime", new Date()) //最后一次修改时间
                        .append("authorCookie", activeCookie) //饼干
                        .append("block", activeBlock) //板块
                        .append("sageSgn", "disable") //世嘉标志
                        .append("lockSgn", "disable") //锁标志
                        .append("hideSgn", "disable") //隐藏标志
                        .append("sageCounter",0) //世嘉请求量
                        .append("sighCounter",0); //浏览量
                System.out.println("Saving post...");
                collection.insertOne(document);
                System.out.println("Done.");
        }

        public void getPostText(String input)
        {
                while(input.length() >= 2000)
                {
                        System.out.println("Too many characters!");
                }
                postText = input;
        }

        public void getActiveCookie(String input)
        {                                                                                                                                                     
                activeCookie = input;
        }

        public void getActiveBlock(String input)
        {
                activeBlock = input;
        }
        
        /* 为着测试, 可以使用这个主函数.*
        public static void main(String args[])
        {
                PostMaker makepost = new PostMaker();
                Scanner gets;
                gets = new Scanner(System.in);
                System.out.println("Give me post text:");
                String testPost = gets.nextLine();
                System.out.println("Give me your cookie:");
                String testCookie = gets.nextLine();
                System.out.println("Give me a block:");
                String testBlock = gets.nextLine();
                makepost.getPostText(testPost);
                makepost.getActiveCookie(testCookie);
                makepost.getActiveBlock(testBlock);
                makepost.savePost();
        }*/
};                                                 
