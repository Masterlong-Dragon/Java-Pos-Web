package pos.modules.pool;

import pos.entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class ResourcePool<T> implements PoolFlush<T>{
        Map<Integer, T> table;
        int poolSize;
        Queue<Integer> IDs;
        ResourcePoolGet<T> get;

        public ResourcePool(int poolSize, ResourcePoolGet<T> get) {
            this.poolSize = poolSize;
            this.get = get;
            table = new HashMap<>();
            IDs = new LinkedBlockingDeque<>();
        }

        // 对象池
        public T get(int ID) throws IllegalArgumentException {
            // 如果在池中，直接返回
            if (table.containsKey(ID)) {
                return table.get(ID);
            }
            // 创建新对象
            else {
                // 如果池满，删除最早的对象
                if (table.size() >= poolSize) {
                    table.remove(IDs.poll());
                }
                // 加入新对象
                // 查询products表
                T product = get.get(ID);
                // 加入池
                table.put(ID, product);
                // 记录ID
                IDs.add(ID);
                return product;
            }
        }

        public void flush( int ID, T obj) {
            table.put(ID, obj);
            IDs.add(ID);
        }

        public boolean contains(int ID) {
            return table.containsKey(ID);
        }

        public void remove(int ID) {
            table.remove(ID);
        }

        public void update(int ID, T product) {
            table.put(ID, product);
        }
}
