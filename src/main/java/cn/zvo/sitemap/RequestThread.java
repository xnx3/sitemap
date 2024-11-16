package cn.zvo.sitemap;

import cn.zvo.sitemap.bean.RequestUrlBean;

public class RequestThread extends Thread{
	public Spider spider;
	
	public boolean isSpider=false; //判断当前是否是正在爬取中，还是sleep。   正在爬取，则是 true
	
	public RequestThread(Spider spider) {
		this.spider = spider;
	}
	
	public void run() {
		
		
		while(spider.runState == 1) {
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			RequestUrlBean urlBean = null;
			try {
				urlBean = spider.getRequestUrl();
			} catch (Exception e) {
				e.printStackTrace();
			}
					
			if(urlBean != null) {
				//有要进行find 的url，执行
				
				//Log.info(this.getName()+ " --  url:"+urlBean.getUrl()+", depth:"+urlBean.getDepth()+", delayRequestUrlMap sie:"+urlUtil.delayRequestUrlMap.size()+" , requestUrlMap:"+urlUtil.requestUrlMap.size());
				
				this.isSpider = true;
				try {
					spider.spider(urlBean.getUrl(), urlBean.getDepth(), this.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					this.isSpider = false;
				}
			}
			
			spider.isEnd();
		}
		spider.end();
	}
	
}
