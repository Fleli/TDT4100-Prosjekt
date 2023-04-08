f$primes$Frederik Edvardsen$7$4$2023$7$4$2023$# Created by Frederik Edvardsen on 7/4/2023.
# This is a .f source code file.
# This code is subject to local copyright law.

int limit = 1000;
int number = 3;

int* primes = alloc(limit / 5); 
heap(primes) = 2;
int count = 1;

println int 2;

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

#int* string_endComment_1 = "Det finnes ";
#int* string_endComment_2 = " primtall mindre enn ";
#int* string_endComment_3 = ".";

print   string          "Det finnes ";  
print   int             count - 1;
print   string          " primtall som er mindre enn ";
print   int             limit; 
println string          "."; 

int a; int b; 

#print string string_endComment_1;
#print int (count - 1);
#print string string_endComment_2;
#print int limit;
#println string string_endComment_3;