package antifraud.RequestAndResponse;

import antifraud.Enums.TransStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponse {

    private TransStatus result;

    private String info;

    public void setTransResp(TransStatus result, String info) {
        if(this.result == null){
            setResult(result);
            setInfo(info);
        } else {
            if(this.result.equals(result)) {
                addInfo(info);
            } else {
                switch (result) {
                    case ALLOWED:
                        break;
                    case MANUAL_PROCESSING:
                        if(this.result == TransStatus.ALLOWED) {
                            setResult(result);
                            setInfo(info);
                        }
                        break;
                    case PROHIBITED:
                        setResult(result);
                        setInfo(info);
                        break;
                }
            }
        }
    }

    public void addInfo(String info) {

        if(this.info == null || this.info.equals(info)) {
            setInfo(info);
        } else {
            this.info = this.info + ", " + info;
        }
    }
}
