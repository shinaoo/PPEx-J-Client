package ppex.utils;

public class NatTypeUtil {
    public enum NatType {
        UNKNOWN(0),
        SYMMETIC_NAT(1),
        PORT_RESTRICT_CONE_NAT(2),
        RESTRICT_CONE_NAT(3),
        FULL_CONE_NAT(4),
        PUBLIC_NETWORK(5),
        ;
        private int value;

        NatType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        public static NatType getByValue(int value){
            for (NatType type : values()){
                if (type.getValue() == value){
                    return type;
                }
            }
            return null;
        }
    }


    public static String getNatStrByValue(int value){
        switch (NatType.getByValue(value)){
            case UNKNOWN:
                return "UNKNOWN";
            case SYMMETIC_NAT:
                return "SYMMETIC_NAT";
            case PORT_RESTRICT_CONE_NAT:
                return "PORT_RESTRICT_CONE_NAT";
            case RESTRICT_CONE_NAT:
                return "RESTRICT_CONE_NAT";
            case FULL_CONE_NAT:
                return "FULL_CONE_NAT";
            case PUBLIC_NETWORK:
                return "PUBLIC_NETWORK";
            default:
                return "";
        }
    }

}
