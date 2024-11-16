package cn.zvo.sitemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xnx3.BaseVO;
import com.xnx3.DateUtil;
import com.xnx3.Log;
import com.xnx3.StringUtil;
import com.xnx3.UrlUtil;

import cn.zvo.http.Http;
import cn.zvo.http.Response;
import cn.zvo.sitemap.bean.RequestUrlBean;

public class Spider {
	public SpiderInterface spiderInterface;
	public ExecuteInterface executeInterface;
	
	/**
	 * 当前运行的名字，可以理解为唯一标识，为了区分不同 spider 运行，主要是进行日志时使用.
	 * 如果不设置，会默认赋予一个随机的当前秒数的字符串
	 */
	public String name;
	
	/**
	 * 当前运行状态
	 * 0 还未开始记录，还在初始化
	 * 1 正在运行中 （深度为1的url获取完网页源代码，并且抓取出它里面的url时，才会变成1）
	 * 2 运行结束
	 */
	public int runState = 0; 
	
	/**
	 * 记录已经获取过网页源代码的url
	 * key ：url绝对路径
	 * value : 当前深度
	 */
	public Map<String, Integer> requestUrlMap = new HashMap<String, Integer>();
	

	/**
	 * 记录当前发现的url，只是为 urlList 排重的作用，无别的意义
	 */
	private Map<String, String> urlMap = new HashMap<String, String>();
	
	/**
	 * 当前执行 spider.start(...) 传入的url 的domain，格式如 www.zvo.cn
	 */
	public String domain;
//
	/**
	 * 将发现的url加入
	 * 这也是执行 spider.start(...) 最后获取到的结果
	 */
	public List<String> urls = new ArrayList<String>();
	
	/**
	 * 向要生成的sitemap.xml中追加一个url
	 * @param url 传入如 http://xxxxx.com/a.html
	 */
	public void addUrl(String url) {
		if(url == null || url.length() < 1) {
			return;
		}
		if(urlMap.get(url) == null) {
			urlMap.put(url, "1");
			urls.add(url);
		}
	}
	
	
	/**
	 * 等待请求的url
	 * key: url
	 * value: 当前这个url的深度
	 */
	public Map<String, Integer> delayRequestUrlMap = new HashMap<String, Integer>();
	
	//深度，默认5
	public int depth = 5;
	
	/**
	 * http请求超时的时间，如果不设置默认是6，单位是秒
	 */
	public int timeout = 6;
	
	/**
	 * http请求超时的时间，如果不设置默认是6，单位是秒
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * 是否忽略网址中携带的get等查询参数，默认是false，不忽略。
	 * 如果是true忽略，那么像是 http://www.zvo.cn/a/b.php?a=1&b=2 会被记录为是 http://www.zvo.cn/a/b.php
	 */
	public boolean ignoreQueryParam = false;
	
	/**
	 * 是否忽略get参数，默认是false，不忽略
	 * 如果是true忽略，那么像是 http://www.zvo.cn/a/b.php?a=1&b=2 会被记录为是 http://www.zvo.cn/a/b.php
	 */
	public void setIgnoreQueryParam(Boolean ignoreQueryParam) {
		this.ignoreQueryParam = ignoreQueryParam;
	}
	
	
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public void setSpiderInterface(SpiderInterface spiderInterface) {
		this.spiderInterface = spiderInterface;
	}
	
	public void setExecuteInterface(ExecuteInterface executeInterface) {
		this.executeInterface = executeInterface;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * 请求源站的方式，默认使用的是1 。回源的方式：  
	 * 1 使用Java本身来模拟请求(速度最快，效率最高，优先建议，但是有的网站设置了防护，这种方式获取不到源码，就不能用了)  
	 * 2 使用本地的 CURL 命令来模拟请求(方式1获取不到的，就用这个吧) 
	 */
	public Short requestSourceType = 1; 
	
	/**
	 * 请求源站的方式，默认使用的是1 。回源的方式：  
	 * 1 使用Java本身来模拟请求(速度最快，效率最高，优先建议，但是有的网站设置了防护，这种方式获取不到源码，就不能用了)  
	 * 2 使用本地的 CURL 命令来模拟请求(方式1获取不到的，就用这个吧) 
	 */
	public void setRequestSourceType(Short requestSourceType) {
		this.requestSourceType = requestSourceType;
	}
	
//	/**
//	 * 记录当前发现的url，最后也就是吧这些一起生成sitemap.xml
//	 */
//	public static Map<String, String> urlMap = new HashMap<String, String>();
	
	
	public int threadNumber; //当前开启的线程数
	public List<RequestThread> requestThreadList; //当前运行的请求线程

	/**
	 * 设置爬取线程
	 * @param threadNumber 开启多少个爬取线程，比如3，便是3个线程去同时抓取网页
	 */
	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}
	
	/**
	 * 开启爬取线程
	 */
	private void threadStart() {
		requestThreadList = new ArrayList<RequestThread>();
		
		for (int i = 0; i < this.threadNumber; i++) {
			RequestThread thread = new RequestThread(this);
			thread.setName("thread "+(i+1));
			thread.start();
			requestThreadList.add(thread);
		}
	}
	
	/**
	 * 主动有 requestThread 触发判断是否是爬取完毕了
	 * 这个是要有线程去触发的
	 */
	public synchronized void isEnd() {
		//线程都运行玩了，在判断一下还有等待要进行抓的网址吗
		if(delayRequestUrlMap.size() > 0) {
			//还有网址没抓，那也是还没完
			return;
		}
		
		for (int i = 0; i < requestThreadList.size(); i++) {
			if(requestThreadList.get(i).isSpider) {
				//还有在运行中的线程，那肯定还没运行完毕
				return;
			}
			//Log.info(requestThreadList.get(i).isSpider+"\t"+requestThreadList.get(i).getName());
		}
		
		//Log.info("jieshu "+requestThreadList.size());
		
		//完事了，标注结束
		this.runState = 2;
	}
	
	public void start(String url) {
//		spider(url, 1);
		List<String> list = new ArrayList<String>();
		list.add(url);
		start(list);
	}
	
	/**
	 * 传入的是一个url的集合，多个url ，而且每个url的深度都是1
	 * @param urls
	 */
	public void start(List<String> urls) {
		this.runState = 0;
		if(urls == null || urls.size() == 0) {
			return;
		}
		this.domain = UrlUtil.getDomain(urls.get(0));
		
		if(this.executeInterface == null) {
			this.setExecuteInterface(new DefaultExecuteInterfaceImpl());
		}
		if(this.name == null) {
			this.name = StringUtil.intTo62(DateUtil.timeForUnix10());
		}
		
		if(urls.size() >= 1) {
			for (int i = 0; i < urls.size(); i++) {
				delayRequestUrlMap.put(urls.get(i), 1);
			}
			
			runState = 1; //设置为开始状态
			//线程开启
			threadStart();
		}
	}
	
	int currendEndThread = 0; //当前已经结束的线程数，正常运行完毕所结束的
	public synchronized void end() {
		currendEndThread++;
		
		//Log.info(" thread end, "+currendEndThread+"/"+threadNumber);
		if(currendEndThread - this.threadNumber == 0) {
			//线程全部都结束了,触发接口的结束
			if(this.spiderInterface != null) {
				this.spiderInterface.end(this);
			}
		}
	}
	
	/**
	 * 通过一个url进行爬取
	 * @param url 要爬取的url
	 * @param depth 深度。 比如设置一个开始的url，那这个url是1， 这个url中再出现的url深度是2
	 * @param 用于执行此的线程名字，它主要是用于记录日志用
	 */
	public void spider(String url, int depth, String threadName) {
		if(depth > this.depth) {
			return;
		}
		
		this.executeInterface.execute(url, depth, threadName, this);
		
		this.spiderInterface.spiderAfter(url, depth,threadName, this);
		
//		if(depth == 1) {
//			//入口url，第一个url
//			
//			if(this.delayRequestUrlMap.size() > 0) {
//				runState = 1; //设置为开始状态
//				//线程开启
//				threadStart();
//			}else {
//				runState = 2; //设置为结束状态，因为开始第一个入口url就根本就没获得到东西
//			}
//		}
	}
	
	
    /**
     * 获取一个要网络请求的url
     * @return
     */
    public synchronized RequestUrlBean getRequestUrl() {
    	RequestUrlBean bean = null;

		for(Map.Entry<String, Integer> entry :this.delayRequestUrlMap.entrySet()){
			String url = entry.getKey();
			Integer depth = entry.getValue();
			if(this.requestUrlMap.get(url) == null) {
				bean = new RequestUrlBean();
				bean.setUrl(url);
				bean.setDepth(depth);
				
				break;
			}
		}
		if(bean != null) {
			//有要进行find 的url，执行
			this.requestUrlMap.put(bean.getUrl(), bean.getDepth());
			this.delayRequestUrlMap.remove(bean.getUrl());
		}
		return bean;
    }
    
    /**
     * 获取当前所有的正在运行的 RequestThread 线程。 这里会吧所有的有关的都会拿到，并不是某一次执行的
     * @return
     */
    public static List<Thread> activityThread() {
    	List<Thread> list = new ArrayList<Thread>();
    	
    	// 获取所有线程的信息
        Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
        
        // 计数特定类的活动线程
        threadMap.keySet().stream()
                .filter(thread -> thread.getClass().equals(RequestThread.class))
                .forEach(thread -> {
                    if (thread.getState() == Thread.State.RUNNABLE ||
                    		thread.getState() == Thread.State.BLOCKED ||
                    				thread.getState() == Thread.State.WAITING ||
                    						thread.getState() == Thread.State.TIMED_WAITING) {
                    	list.add(thread);
                    }
                });
        
       return list; 
    }
}
