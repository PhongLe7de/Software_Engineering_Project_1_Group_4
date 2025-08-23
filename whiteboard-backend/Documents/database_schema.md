# Database Schema

## Tables

### user
- **id** (Primary Key)
- **uuid**
- **email**
- **username**

### draw_event
- **id** (Primary Key)
- **user_id** (Foreign Key → user.id)
- **username**
- **timestamp**
- **type**
- **tool**
- **coordination**
- **color**

## Visual Schema

```
┌─────────────────┐      ┌─────────────────────┐
│      user       │      │    draw_event       │
├─────────────────┤      ├─────────────────────┤
│ id (PK)         │◄──── ┤ id (PK)             │
│ uuid            │      │ user_id (FK)        │
│ email           │      │ username            │
│ username        │      │ timestamp           │
└─────────────────┘      │ type                │
                         │ tool                │
                         │ coordination        │
                         │ color               │
                         └─────────────────────┘
```

## Relationships
- One **user** can have many **draw_event** records.
