primary_expression
	: 'id'
	| 'number'
	| 'literal'
	| 'left_pare' expression 'right_pare'
	;

unary_expression
    : primary_expression
	| 'add_op' unary_expression
	| 'unary_op' unary_expression
	;

multiplicative_expression
	: unary_expression
	| multiplicative_expression 'mul_op' unary_expression
	;

additive_expression
	: multiplicative_expression
	| additive_expression 'add_op' multiplicative_expression
	;

shift_expression
	: additive_expression
	| shift_expression 'shift_op' additive_expression
	;

relational_expression
	: shift_expression
	| relational_expression 'compare_op' shift_expression
	;

equality_expression
	: relational_expression
	| equality_expression 'equality_op' relational_expression
	;

bool_expression
	: equality_expression
	| 'exclaim'  bool_expression
	| bool_expression 'bool_op' equality_expression
	;

declaration
    : 'primitive_type'  'id'
    ;

declaration_stmt
    : declaration 'semicolon'
    ;

assignment_stmt
	: 'id' 'assignment' bool_expression 'semicolon'
	| declaration 'assignment' bool_expression 'semicolon'
	;

if_else_stmt
    : 'if' 'left_pare' bool_expression 'right_pare' 'left_cur_bra' stmts 'right_cur_bra' 'else' 'left_cur_bra' stmts 'right_cur_bra'
    | 'if' 'left_pare' bool_expression 'right_pare' 'left_cur_bra' stmts 'right_cur_bra'
    ;

while_stmt
    : 'while' 'left_pare' bool_expression 'right_pare' 'left_cur_bra' stmts 'right_cur_bra'
    ;

stmt
	: assignment_stmt
	| declaration_stmt
	| if_else_stmt
	| while_stmt
	;

stmts
    : stmts stmt
    | stmt
    ;

grammar
    : stmts
    ;