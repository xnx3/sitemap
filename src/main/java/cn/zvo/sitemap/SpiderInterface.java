package cn.zvo.sitemap;

/**
 * 爬虫抓取完毕后触发的
 * @author 管雷鸣
 *
 */
public interface SpiderInterface {
	
	/**
	 * 爬虫抓取完毕后触发
	 */
	public void end(Spider spider);
	
	/**
	 * 爬取一个url结束之后触发
	 * @param url 爬完的url
	 * @param depth 深度。 比如设置一个开始的url，那这个url是1， 这个url中再出现的url深度是2
	 * @param 用于执行此的线程名字，它主要是用于记录日志用
	 */
	public void spiderAfter(String url, int depth,String threadName, Spider spider);
	
}
