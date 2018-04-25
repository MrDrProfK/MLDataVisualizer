package words;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WordCounter {
    private final List<CountedWord> words;
    private final HashMap<Word, CountedWord> wordsForFinding;

    WordCounter() {
	words = new LinkedList<>();
        wordsForFinding = new HashMap<>();
    }
    
    private CountedWord findWord(Word w) {
        return wordsForFinding.get(w);
//        for(int i = 0; i < words.size(); i++) {
//            CountedWord cw = words.get(i);
//            if(cw.getWord().equals(w))
//                return cw;
//        }
//        return null;
    }

    void countWord(Word w) {
        CountedWord cw = findWord(w);
	if(cw == null) {
	    cw = new CountedWord(w);
            wordsForFinding.put(w, cw);
            words.add(cw);
        }
	cw.tally();
    }

    int getCount(Word w) {
	CountedWord cw = findWord(w);
	if(cw == null)
	    return(0);
	else
	    return(cw.getCount());
    }

    int numWords() {
	return(words.size());
    }

    CountedWord [] sortWords() {
        CountedWord[] wds = words.toArray(new CountedWord[0]);
	Quicksort.quickSort(wds);
	return(wds);
    }

}
