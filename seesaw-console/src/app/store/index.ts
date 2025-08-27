import { configureStore, type EnhancedStore } from '@reduxjs/toolkit'
import authenticationReducer from '@/app/store/authenticationSlice'

const store: EnhancedStore = configureStore({
  reducer: {
    authentication: authenticationReducer
  }
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
export default store
