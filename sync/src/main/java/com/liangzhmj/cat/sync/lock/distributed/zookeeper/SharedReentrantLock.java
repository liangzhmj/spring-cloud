package com.liangzhmj.cat.sync.lock.distributed.zookeeper;

import com.liangzhmj.cat.sync.lock.exception.SyncException;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.RevocationListener;

import java.util.concurrent.TimeUnit;

/**
 * 可重入锁
 * @author Administrator
 *
 */
@Log4j2
public class SharedReentrantLock {


	private InterProcessMutex lock;
	//用makeRevocable()将锁设为可撤销的. 当别的进程或线程想让你释放锁时Listener会被调用
	private RevocationListener<InterProcessMutex> revocationListener;  
	 
	public SharedReentrantLock(CuratorFramework cf, String path){
		this.lock = new InterProcessMutex(cf, path);
		revocationListener = new RevocationListener<InterProcessMutex>() {  
            @Override  
            public void revocationRequested(InterProcessMutex forLock) {  
            	log.info("revocationRequested被调用 isAcquiredInThisProcess:"+forLock.isAcquiredInThisProcess());
                if(!forLock.isAcquiredInThisProcess()){  
                    return;  
                }  
                try{  
                    forLock.release();  
                }catch(Exception e){  
                    e.printStackTrace();  
                }  
            }  
        };
        this.lock.makeRevocable(revocationListener);
	}
	
	public void lock() throws Exception{
		try {
			this.lock.acquire();
		} catch (Exception e) {
			throw new SyncException("请求锁出现异常:"+e.getMessage());
		}
	}
	
	public boolean tryLock(long time,TimeUnit unit) throws Exception{
		return this.lock.acquire(time, unit);
	}
	
	public boolean isAcquiredInThisProcess(){
		return lock.isAcquiredInThisProcess();
	}
	
	public void unlock() throws Exception{
		try {
			this.lock.release();
		} catch (Exception e) {
			throw new SyncException("请求释放锁出现异常:"+e.getMessage());
		}
	}

	@Override
	public String toString() {
		return lock.toString();
	}
	
	
	
}
