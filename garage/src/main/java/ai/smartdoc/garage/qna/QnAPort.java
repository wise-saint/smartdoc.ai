package ai.smartdoc.garage.qna;

public interface QnAPort {

    String askQuestion(String docId, String question);
}
