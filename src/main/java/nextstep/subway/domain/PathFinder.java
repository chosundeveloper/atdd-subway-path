package nextstep.subway.domain;

import lombok.Getter;
import nextstep.subway.applicaion.dto.PathResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@Getter
public class PathFinder {

    DijkstraShortestPath<Station, DefaultWeightedEdge> subwayGraph;

    public PathFinder() {

    }

    public PathFinder(List<Line> 노선목록) {
        this.subwayGraph = new DijkstraShortestPath<>(createSubwayGraph(노선목록));
    }

    public static PathFinder of(List<Line> 노선목록) {
        return new PathFinder(노선목록);
    }

    public PathResponse paths(Station source, Station target) {
        validSearchPath(source, target);
        GraphPath<Station, DefaultWeightedEdge> paths = subwayGraph.getPath(source, target);
        if (paths == null) {
            throw new IllegalArgumentException();
        }
        return new PathResponse(paths.getVertexList(), (int) paths.getWeight());
    }

    private void validSearchPath(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException("출발역과 도착역이 같을 수 없습니다.");
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createSubwayGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> subwayGraph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        lines.forEach(line -> createSectionGraph(subwayGraph, line.sections()));
        return subwayGraph;
    }

    private void createSectionGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

}
