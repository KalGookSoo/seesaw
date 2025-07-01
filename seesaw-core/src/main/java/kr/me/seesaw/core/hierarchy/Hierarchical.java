package kr.me.seesaw.core.hierarchy;

public interface Hierarchical<T extends Hierarchical<T, ID>, ID> {

    ID getId();

    ID getParentId();

    void addChild(T child);

    default boolean isRoot() {
        return getParentId() == null;
    }

    default boolean hasParent() {
        return !isRoot();
    }

}
