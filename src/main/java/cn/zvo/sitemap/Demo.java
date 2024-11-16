package cn.zvo.sitemap;

public class Demo {
	public static void main(String[] args) {
		Spider spider = new Spider();
		spider.setThreadNumber(20); //开启20个抓取线程
		spider.setDepth(2);			//目录深度是2
		spider.setTimeout(10);		//超时时间是 10 秒
		//日志输出
		spider.setSpiderInterface(new SpiderInterface() {
			public void end(Spider spider) {
				/*
				 * spider.urls 最终抓取到的 spider.start(...) 传入的网站的url列表
				 * spider.requestUrlMap 记录已经获取过网页源代码的url
				 * spider.domain 当前执行 spider.start(...) 传入的url 的domain，格式如 www.zvo.cn
				 * spider.delayRequestUrlMap 等待请求的url
				 * spider.depth 抓取深度。比如 spider.start(url) 设置一个开始的url，那这个url是1， 这个url中再出现的url深度是2 。如果你只抓取当前url中存在的当前网站的连接，那这里就设置1。 如果不设置，这里默认是0。 不建议超过10，数字越大损耗会越大。如果是0那么代表一直往下抓取，也就是所有出现过的页面都会抓取一边（如果你有一千万个页面，那这一千万个页面都会取打开抓一边，对性能影响还是有的）
				 * spider.threadNumber 当前开启的线程数量
				 * spider.requestThreadList 当前运行的请求线程
				 * spider.timeout http请求超时的时间，如果不设置默认是6，单位是秒
				 * spider.ignoreQueryParam 是否忽略网址中携带的get等查询参数，默认是false，不忽略。 如果是true忽略，那么像是 http://www.zvo.cn/a/b.php?a=1&b=2 会被记录为是 http://www.zvo.cn/a/b.php
				 */
				System.out.println("获取到url数量："+spider.urls.size());
				System.out.println(spider.urls);
			}
			public void spiderAfter(String url, int depth,String threadName, Spider spider) {
				System.out.println("当前执行到： "+url+"\t深度:"+depth+"\t"+spider.delayRequestUrlMap.size()+"/"+(spider.requestUrlMap.size()+spider.delayRequestUrlMap.size()));
			}
		});
		//启动，开始抓取
		spider.start("https://gitee.com/");
	}
}
