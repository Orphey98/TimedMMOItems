# TimedMMOItems

Added stats for making expirable MMOItems stuff 
- Expiry Period: how long is the item usable (in seconds)
- Expiry Date: the deadline for item (in seconds, since Unix Epoch)

You should only care the first stat which is Expiry Period. All items will be automatically checked and the second stat will be added when necessary.

Bypass permission: *timeditems.bypass*
