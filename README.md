# DB_assign5

## Changes Made

### UserOverview DTO
Counstructer added

## Error:

SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
SLF4J: Failed to load class "org.slf4j.impl.StaticMDCBinder".
SLF4J: Defaulting to no-operation MDCAdapter implementation.
SLF4J: See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.

I believe this Error gets in the way of succesfully passing all of the tests, as i tried
removing the null/falls checks to execute the redis commands immediatly. Also tested the same
redis funtions in the redis client terminal.  