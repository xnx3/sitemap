# sitemap
传入一个网址，自动扫出这个网址内，同域名的所有URL。

## 能力
* 可以指定抓取深度，比如指定的网址进入爬取时深度是1， 这里面爬取到的几十个网址再逐个进入继续爬取深度是2，  深度指定无上限
* 自动过滤掉超链接的描点
* 支持多线程能力，比如你可以启用100个线程同时去抓，当然线程多了容易被目标网站禁止访问，也或者吧对方网站给抓宕机了。
* 支持自动排重，对抓取的URL进行排重处理
* 支持自动过滤非本域名下的url。 抓取的网址只保留跟最开始传入的网址同域名的。
* 支持输出 SDK开发包 的形式，有第三方系统接入本能力。

## SDK开发包

#### 1. maven 引入
````
<dependencies>
	<groupId>cn.zvo.sitemap</groupId>
	<artifactId>sitemap</artifactId>
	<version>1.0</version>
</dependencies>
````

#### 2. 开发使用

````
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
````