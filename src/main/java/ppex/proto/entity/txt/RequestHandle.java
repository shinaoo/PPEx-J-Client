package ppex.proto.entity.txt;

public interface RequestHandle {
    default Response handleRequest(Request request){
        return null;
    }
}