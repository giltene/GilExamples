# MinorGC
----------------------------------------------------------------------------

Written by Gil Tene of Azul Systems, and released to the public domain
as explained at http://creativecommons.org/publicdomain/zero/1.0

----------------------------------------------------------------------------

MinorGC is a simple tool that forces frequent minor GC events to occur
(by constantly allocating objects that immediately die) while also forcing
the OldGen to collect periodically. It is useful for demonstrating the range
of newgen pause times across the full lifecycle of oldgen promotion/collection.