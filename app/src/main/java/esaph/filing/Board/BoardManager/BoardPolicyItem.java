package esaph.filing.Board.BoardManager;

import esaph.filing.Board.Model.BoardPolicy;

public class BoardPolicyItem
{
    private BoardPolicy boardPolicy;
    private String mPolicyName;
    private String mPolicyDescription;
    private int mImgId;

    public BoardPolicyItem(BoardPolicy boardPolicy, String mPolicyName, String mPolicyDescription, int mImgId) {
        this.boardPolicy = boardPolicy;
        this.mPolicyName = mPolicyName;
        this.mPolicyDescription = mPolicyDescription;
        this.mImgId = mImgId;
    }

    public BoardPolicy getBoardPolicy() {
        return boardPolicy;
    }

    public String getmPolicyName() {
        return mPolicyName;
    }

    public String getmPolicyDescription() {
        return mPolicyDescription;
    }

    public int getmImgId() {
        return mImgId;
    }
}
