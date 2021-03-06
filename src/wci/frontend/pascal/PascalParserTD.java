package wci.frontend.pascal;

import wci.frontend.*;
import wci.message.Message;

import java.io.IOException;

import static wci.frontend.pascal.PascalErrorCode.IO_ERROR;
import static wci.frontend.pascal.PascalTokenType.ERROR;
import static wci.message.MessageType.PARSER_SUMMARY;
import static wci.message.MessageType.TOKEN;

/**
 * <h1>PascalParserTD</h1>
 *
 * <p>The top-down Pascal parser.</p>
 */
public class PascalParserTD extends Parser {
    protected static PascalErrorHandler errorHandler = new PascalErrorHandler();
    /**
     * Constructor.
     * @param scanner the scanner to be used with this parser.
     */
    public PascalParserTD(Scanner scanner)
    {
        super(scanner);
    }
    /**
     * Parse a Pascal source program and generate the symbol table
     * and the intermediate code.
     */
    @Override
    public void parse() throws Exception {
        Token token;
        long startTime = System.currentTimeMillis();
        try{
            while (!((token = nextToken()) instanceof EofToken)) {
                TokenType tokenType = token.getType();
                if(tokenType != ERROR){
                    // Format each token.
                    sendMessage(new Message(TOKEN,
                            new Object[] {token.getLineNumber(),
                                    token.getPosition(),
                                    tokenType,
                                    token.getText(),
                                    token.getValue()}));
                }
                else
                {
                    errorHandler.flag(token,(PascalErrorCode)token.getValue(),this);
                }
            }
            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            sendMessage(new Message(PARSER_SUMMARY,
                    new Number[] {token.getLineNumber(),
                            getErrorCount(),
                            elapsedTime}));
        }catch (IOException ex){
            errorHandler.abortTranslation(IO_ERROR, this);
        }

    }
    /**
     * Return the number of syntax errors found by the parser.
     * @return the error count.
     */
    @Override
    public int getErrorCount() {
        return errorHandler.getErrorCount();
    }
}
