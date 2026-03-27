package com.freesia.chain;

import com.freesia.chain.handler.AbstractCheckColumnHandler;
import com.freesia.chain.impl.CheckColumnDetailHandler;
import com.freesia.chain.impl.CheckColumnHeaderHandler;
import com.freesia.chain.impl.CheckColumnMiddleHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Bliss.Wu
 * @Description 检查系统列配置是否完善 责任链
 * @date 2026-03-27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckColumnChain {
    public static AbstractCheckColumnHandler build() {
        Queue<AbstractCheckColumnHandler> sequence = new LinkedList<>();
        sequence.add(new CheckColumnHeaderHandler());
        sequence.add(new CheckColumnMiddleHandler());
        sequence.add(new CheckColumnDetailHandler());
        AbstractCheckColumnHandler peek = sequence.poll();
        AbstractCheckColumnHandler peek1 = peek;
        sequence.poll();
        for (AbstractCheckColumnHandler current : sequence) {
            peek.setNext(current);
            peek = current;
        }
        return peek;
    }
}