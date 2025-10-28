import { createFileRoute, Navigate } from '@tanstack/react-router'

/*
 Navigation Flow:
    - Join Board → Joins board + navigates to
  /board/$boardId
    - Open Board → Navigates directly to
  /board/$boardId
    - Root URL "/"  → Redirects to /home
 */
function Index() {
    return <Navigate to="/home" />;
}

export const Route = createFileRoute('/')({
    component: Index,
})