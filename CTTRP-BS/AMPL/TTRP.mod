# Declaración de conjuntos simples
set K;
set T;
set C;

# Declaración de conjuntos complejos
set BT{T};
set B:= union {t in T} BT[t];
set RK{K};
set RC{C};
set RB{B};
set RKC{k in K, c in C}:= setof{r1 in RK[k], r2 in RC[c]: r1 = r2} r1;
set RKB{k in K, b in B}:= setof{r1 in RK[k], r2 in RB[b]: r1 = r2} r1;
set RKBT{k in K, t in T, b in BT[t]}:= setof{r1 in RK[k], r2 in RB[b]: r1 = r2} r1;
set R:= union {k in K} RK[k];

# Parámetros del modelo.
param n{K};        # Máximo de vehículos disponibles por cada tipo.
param cr{R} > 0;   # Costo de transporte de cada ruta.
param f{B};        # Costo de apertura de cada bahía.  
param ir{R};       # Ingreso por ruta

# Variables de decisión.
var y{b in B} binary;                # Si la bahía b se abre o no
var x{k in K, r in RK[k]} binary;    # Si el vehiuclo de tipo k se asigna o no a la ruta r
var h{C} binary;

# Función Objetivo.
minimize z0: sum{k in K, r in RK[k]} cr[r]*x[k,r] + sum{b in B} f[b]*y[b];
#maximize z1: sum{k in K, r in RK[k]} ir[r]*x[k,r] - sum{b in B} f[b]*y[b];

# Definición de las restricciones

# Restricciones de limites inferiores y superiores de frecuencias por tipo de línea 
subject to VehKAva{k in K}: sum{r in RK[k]} x[k,r] <= n[k];

subject to Sat0{c in C}: sum{k in K, r in RKC[k,c]} x[k,r] = 1;
#subject to Sat1{c in C}: sum{k in K, r in RKC[k,c]} x[k,r] + h[C] = 1;

#subject to Coord{t in T, b in BT[t]}: sum{k in K, r in RKBT[k,t,b]} x[k,r] <= 1;
subject to rute2bay{k in K, b in B, r in RKB[k,b]}: x[k,r] <= y[b];

