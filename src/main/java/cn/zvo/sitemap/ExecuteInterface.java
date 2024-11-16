package cn.zvo.sitemap;

/**
 * spider 实际执行的接口
 * @author 管雷鸣
 *
 */
public interface ExecuteInterface {
	
	public void execute(String url,int depth,String threadName, Spider spider);
}
