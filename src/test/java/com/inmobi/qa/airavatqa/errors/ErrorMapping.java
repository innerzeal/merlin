/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inmobi.qa.airavatqa.errors;

/**
 *
 * @author rishu.mehrotra
 */
public enum ErrorMapping {
    
    NO_RETRY_SPECIFIED("NoRetrySpecified.xml","javax.xml.bind.UnmarshalException - with linked exception:[org.xml.sax.SAXParseException; lineNumber: 54; columnNumber: 67; cvc-complex-type.2.4.a: Invalid content was found starting with element 'late-process'. One of '{retry}' is expected.]"),
    NO_CONCURRENCY_PARAM("noConcurrencyParam.xml","javax.xml.bind.UnmarshalException - with linked exception:[org.xml.sax.SAXParseException; lineNumber: 29; columnNumber: 16; cvc-complex-type.2.4.a: Invalid content was found starting with element 'execution'. One of '{concurrency}' is expected.]"),
    NO_EXECUTION_SPECIFIED("noExecutionSpecified.xml","javax.xml.bind.UnmarshalException - with linked exception:[org.xml.sax.SAXParseException; lineNumber: 29; columnNumber: 16; cvc-complex-type.2.4.a: Invalid content was found starting with element 'frequency'. One of '{execution}' is expected.]"),
    NO_WORKFLOW_PARAMS("NoWorkflowParams.xml","javax.xml.bind.UnmarshalException - with linked exception:[org.xml.sax.SAXParseException; lineNumber: 52; columnNumber: 71; cvc-complex-type.2.4.a: Invalid content was found starting with element 'retry'. One of '{workflow}' is expected.]"),
    PROCESS_INVALID("process-invalid.xml","javax.xml.bind.UnmarshalException - with linked exception:[org.xml.sax.SAXParseException; lineNumber: 2; columnNumber: 72; cvc-elt.1: Cannot find the declaration of element 'Process'.]"),
    SUBMIT_INVALID("inValid01_sameName.xml","sample already exists"),
    RESUBMIT_PROCESS("inValid02_sameName.xml","sample already exists");
    
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private ErrorMapping(String filename, String errorMessage) {
        this.filename = filename;
        this.errorMessage = errorMessage;
    }
    
    String filename;
    String errorMessage;
    
    
    
    
    
}
