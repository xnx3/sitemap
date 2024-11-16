package cn.zvo.sitemap.bean;

/**
 * 从待请求URL队列中，获取一个
 * @author 管雷鸣
 *
 */
public class RequestUrlBean {
	private String url;	//待请求的URL
	private int depth; 	//深度
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}
