package jira

import spock.lang.Specification

class AddJiraLinksTest extends Specification {

    def 'addJiraLinks() - html'() {
        given:
        def options = new AddJiraLinks.Options(html: true, markDown: false, jira: false)

        expect:
        AddJiraLinks.addJiraLinks(input, options) == expectedOutput

        where:
        input                                   || expectedOutput
        ['IEI-1234']                            || ['<a href=\"http://jiraprod.agfahealthcare.com/browse/IEI-1234\">IEI-1234</a>'].join('\n')
        ['IEIBUILD-5678']                       || ['<a href=\"http://jiraprod.agfahealthcare.com/browse/IEIBUILD-5678\">IEIBUILD-5678</a>'].join('\n')
        ['IPTS-91011']                          || ['<a href=\"http://jiraprod.agfahealthcare.com/browse/IPTS-91011\">IPTS-91011</a>'].join('\n')
        ['NoJiraKeyHere']                       || ['NoJiraKeyHere'].join('\n')
        ['Multiple keys IEI-1234 and IPTS-91011'] || ['Multiple keys <a href=\"http://jiraprod.agfahealthcare.com/browse/IEI-1234\">IEI-1234</a> and <a href=\"http://jiraprod.agfahealthcare.com/browse/IPTS-91011\">IPTS-91011</a>'].join('\n')
        ['Mixed IEI-1234 text IEIBUILD-5678 and IPTS-91011!'] || ['Mixed <a href=\"http://jiraprod.agfahealthcare.com/browse/IEI-1234\">IEI-1234</a> text <a href=\"http://jiraprod.agfahealthcare.com/browse/IEIBUILD-5678\">IEIBUILD-5678</a> and <a href=\"http://jiraprod.agfahealthcare.com/browse/IPTS-91011\">IPTS-91011</a>!'].join('\n')
        ['  IEI-1234  ']                        || ['<a href=\"http://jiraprod.agfahealthcare.com/browse/IEI-1234\">IEI-1234</a>'].join('\n')
        ['', '   ', '\t']                       || ''.join('\n')
    }

    def 'addJiraLinks() - markDown'() {
        given:
        def options = new AddJiraLinks.Options(html: false, markDown: true, jira: false)

        expect:
        AddJiraLinks.addJiraLinks(input, options) == expectedOutput

        where:
        input                                   || expectedOutput
        ['IEI-1234']                            || ['[IEI-1234](http://jiraprod.agfahealthcare.com/browse/IEI-1234)'].join('\n')
        ['IEIBUILD-5678']                       || ['[IEIBUILD-5678](http://jiraprod.agfahealthcare.com/browse/IEIBUILD-5678)'].join('\n')
        ['IPTS-91011']                          || ['[IPTS-91011](http://jiraprod.agfahealthcare.com/browse/IPTS-91011)'].join('\n')
        ['NoJiraKeyHere']                       || ['NoJiraKeyHere'].join('\n')
        ['Multiple keys IEI-1234 and IPTS-91011'] || ['Multiple keys [IEI-1234](http://jiraprod.agfahealthcare.com/browse/IEI-1234) and [IPTS-91011](http://jiraprod.agfahealthcare.com/browse/IPTS-91011)'].join('\n')
        ['Mixed IEI-1234 text IEIBUILD-5678 and IPTS-91011!'] || ['Mixed [IEI-1234](http://jiraprod.agfahealthcare.com/browse/IEI-1234) text [IEIBUILD-5678](http://jiraprod.agfahealthcare.com/browse/IEIBUILD-5678) and [IPTS-91011](http://jiraprod.agfahealthcare.com/browse/IPTS-91011)!'].join('\n')
        ['  IEI-1234  ']                        || ['[IEI-1234](http://jiraprod.agfahealthcare.com/browse/IEI-1234)'].join('\n')
        ['', '   ', '\t']                       || ''.join('\n')
    }

    def 'addJiraLinks() - jira'() {
        given:
        def options = new AddJiraLinks.Options(html: false, markDown: false, jira: true)

        expect:
        AddJiraLinks.addJiraLinks(input, options) == expectedOutput

        where:
        input                                   || expectedOutput
        ['IEI-1234']                            || ['[IEI-1234|http://jiraprod.agfahealthcare.com/browse/IEI-1234]'].join('\n')
        ['IEIBUILD-5678']                       || ['[IEIBUILD-5678|http://jiraprod.agfahealthcare.com/browse/IEIBUILD-5678]'].join('\n')
        ['IPTS-91011']                          || ['[IPTS-91011|http://jiraprod.agfahealthcare.com/browse/IPTS-91011]'].join('\n')
        ['NoJiraKeyHere']                       || ['NoJiraKeyHere'].join('\n')
        ['Multiple keys IEI-1234 and IPTS-91011'] || ['Multiple keys [IEI-1234|http://jiraprod.agfahealthcare.com/browse/IEI-1234] and [IPTS-91011|http://jiraprod.agfahealthcare.com/browse/IPTS-91011]'].join('\n')
        ['Mixed IEI-1234 text IEIBUILD-5678 and IPTS-91011!'] || ['Mixed [IEI-1234|http://jiraprod.agfahealthcare.com/browse/IEI-1234] text [IEIBUILD-5678|http://jiraprod.agfahealthcare.com/browse/IEIBUILD-5678] and [IPTS-91011|http://jiraprod.agfahealthcare.com/browse/IPTS-91011]!'].join('\n')
        ['  IEI-1234  ']                        || ['[IEI-1234|http://jiraprod.agfahealthcare.com/browse/IEI-1234]'].join('\n')
        ['', '   ', '\t']                       || ''.join('\n')
    }
}
