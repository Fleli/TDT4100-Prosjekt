f$primes$Frederik Edvardsen$7$4$2023$7$4$2023$# Created by Frederik Edvardsen on 7/4/2023.
# This is a .f source code file.
# This code is subject to local copyright law.

int limit = 1000;
int number = 3;

int* primes = alloc(limit / 6);
heap(primes) = 2;
int count = 1;

while number < limit {
    
    int isPrime = 1;
    int factor = 2;
    
    while factor < number {
        
        # Ordne feilen med at feilsymbol dukker opp, uten at feil*meldinger* dukker opp
        # F.eks. bare ved å skrive {} en plass.
        
        # Videre: Left curly brace bør ikke lage en right curly inne i en kommentar (lol)
        
        # En annen feil: Scrolling ser ikke ut til å fungere 100 prosent
        # korrekt når ingen linje er valgt. Undersøk dette.
        
        if (number % factor == 0) {
            isPrime = 0;
            factor = number;
        } else if 1 {
            factor = factor + 1;
        }
        
    }
    
    if isPrime {
        println int number;
        heap(primes + count) = number;
        count = count + 1;
    }
    
    number = number + 2;
    
}