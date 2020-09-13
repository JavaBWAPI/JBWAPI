package bwapi;


class Cache<T> {
    private int frame = -1;
    private T obj;

    void set(T obj, int frame) {
        this.frame = frame;
        this.obj = obj;
    }

    boolean valid(int currentFrame) {
        return frame == currentFrame;
    }

    T get() {
        return obj;
    }

    @Override
    public String toString() {
        return obj != null ? obj.toString() : "null";
    }
}

class IntegerCache extends Cache<Integer> {

    void setOrAdd(int obj, int frame) {
        if (valid(frame)) {
            set(get() + obj, frame);
        }
        else {
            set(obj, frame);
        }
    }
}

class BooleanCache extends Cache<Boolean>{}
class OrderCache extends Cache<Order>{}
class UnitTypeCache extends Cache<UnitType>{}
class UpgradeTypeCache extends Cache<UpgradeType>{}
class TechTypeCache extends Cache<TechType>{}