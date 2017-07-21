package com.bayin.library.view;

import java.util.Observable;

/****************************************
 * 功能说明:  
 *
 * Author: Created by bayin on 2017/6/22.
 ****************************************/

public class OverScrollObservable extends Observable {
    public static final int MSG_LOADMORE = 1;
    public static final int MSG_RESET = 2;

    /**
     * 加载下一页
     */
    public void notifyLoadMore(){
        setChanged();
        notifyObservers(MSG_LOADMORE);
    }

    /**
     * 回到上一页
     */
    public void notifyReset(){
        setChanged();
        notifyObservers(MSG_RESET);
    }
}
