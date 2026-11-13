import { Route, Routes } from 'react-router-dom' 
import Layout from './components/layout/Layout.jsx' 
import HomePage from './pages/HomePage.jsx' 
import CatalogPage from './pages/CatalogPage.jsx' 
import VehicleDetailPage from './pages/VehicleDetailPage.jsx' 
import ConfiguratorPage from './pages/ConfiguratorPage.jsx' 
import HotDealsPage from './pages/HotDealsPage.jsx' 
import ComparePage from './pages/ComparePage.jsx' 
import CalculatorsPage from './pages/CalculatorsPage.jsx' 
import LoginPage from './pages/LoginPage.jsx' 
import RegisterPage from './pages/RegisterPage.jsx' 
import CartPage from './pages/CartPage.jsx' 
import CheckoutPage from './pages/CheckoutPage.jsx' 
import TestDrivePage from './pages/TestDrivePage.jsx' 
import RecommendationsPage from './pages/RecommendationsPage.jsx' 
import AdminDashboardPage from './pages/admin/AdminDashboardPage.jsx' 
import NotFoundPage from './pages/NotFoundPage.jsx' 

// All routes in one place. 
export default function App() { 
  return ( 
    <Routes> 
      <Route element={<Layout />}> 
        <Route index element={<HomePage />} /> 
        <Route path="vehicles" element={<CatalogPage />} /> 
        <Route path="vehicles/:id" element={<VehicleDetailPage />} /> 
        <Route path="vehicles/:id/configure" element={<ConfiguratorPage />} /> 
        <Route path="vehicles/:id/loan" element={<CalculatorsPage />} /> 
        <Route path="vehicles/:id/savings" element={<CalculatorsPage />} /> 
        <Route path="vehicles/:id/test-drive" element={<TestDrivePage />} /> 
        <Route path="hot-deals" element={<HotDealsPage />} /> 
        <Route path="compare" element={<ComparePage />} /> 
        <Route path="calculators" element={<CalculatorsPage />} /> 
        <Route path="recommendations" element={<RecommendationsPage />} /> 
        <Route path="login" element={<LoginPage />} /> 
        <Route path="register" element={<RegisterPage />} /> 
        <Route path="cart" element={<CartPage />} /> 
        <Route path="checkout" element={<CheckoutPage />} /> 
        <Route path="admin" element={<AdminDashboardPage />} /> 
        <Route path="*" element={<NotFoundPage />} /> 
      </Route> 
    </Routes> 
  ) 
} 
