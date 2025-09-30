package ai.smartdoc.garage.qna;

public interface QnAPort {

    Object askQuestion(String docId, String question);
}
