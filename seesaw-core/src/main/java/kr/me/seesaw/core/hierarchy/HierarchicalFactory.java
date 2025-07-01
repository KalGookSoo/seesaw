package kr.me.seesaw.core.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface HierarchicalFactory {

    static <T extends Hierarchical<T, ID>, ID> T mapChildren(T parentNode, Map<ID, List<T>> nodesGroupByParent) {
        List<T> children = nodesGroupByParent.getOrDefault(parentNode.getId(), new ArrayList<>());
        List<T> nestedChildren = children.stream()
                .map(child -> mapChildren(child, nodesGroupByParent))
                .toList();
        nestedChildren.forEach(parentNode::addChild);
        return parentNode;
    }

    static <T extends Hierarchical<T, ID>, ID> List<T> build(List<T> elements) {
        Map<ID, List<T>> collect = elements.stream()
                .filter(T::hasParent)
                .collect(Collectors.groupingBy(T::getParentId));
        return elements.stream()
                .filter(T::isRoot)
                .map(element -> mapChildren(element, collect))
                .collect(Collectors.toList());
    }

}
