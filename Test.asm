Data1 Segment 
		vыs db 11hп
		var2 dw 45р67h
		str1 db "Гыч"
Data1 Ends

Data2 Segment
		var3 dd 0fdah
		var4 dw 322h
Data2 Ends

var	equ	var3	
cost equ strt2
sss equ [edx+2h]
ss1 equ var3[si + 20h]

Code1 Segment
assume	cs:Code1, ds:Data1, es:Data2
strt1:	cli

		dec eax
		dec ax
		dec al
		
		jmp cost
		
		pop  var2 sss
		
		cmp eax, ebx
		cmp ax, bx
		cmp al, bl
		
		or eax, es:ss1
		or eax, es:var3[si+ 2gh]
		or al, var1[di+4h]
		
		js l1
		mov edx, 100h
		lds si, var[esp]
		
l1:		jmp var3
Code1 ends

Code2 Segment
assume	cs:Code2, ds:Data1, es:Data2 
strt2:  shr var2[edx], cl
		jmp strt1
		
		mov dx, 5634h
		
		lds bx, var3[esp]
		
		js strt2
Code2 ends
end