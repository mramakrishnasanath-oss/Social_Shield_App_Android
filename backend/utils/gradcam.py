"""Grad-CAM heatmap generator for explainable AI"""
import torch
import numpy as np
import logging

logger = logging.getLogger(__name__)


class GradCAMGenerator:
    """Generate Grad-CAM visualization for model explanations"""
    
    def __init__(self, model, target_layer_name: str):
        self.model = model
        self.target_layer = self._find_layer(model, target_layer_name)
        self.gradients = None
        self.activations = None
        self._register_hooks()
    
    def _find_layer(self, model, layer_name: str):
        """Find layer by dot-notation name"""
        parts = layer_name.split('.')
        layer = model
        for part in parts:
            try:
                layer = getattr(layer, part)
            except AttributeError:
                # Try integer index
                try:
                    layer = layer[int(part)]
                except (TypeError, IndexError, ValueError):
                    logger.warning(f"Layer {layer_name} not found")
                    return None
        return layer
    
    def _register_hooks(self):
        if self.target_layer is None:
            return
        
        def forward_hook(module, input, output):
            self.activations = output.detach()
        
        def backward_hook(module, grad_input, grad_output):
            self.gradients = grad_output[0].detach()
        
        self.target_layer.register_forward_hook(forward_hook)
        self.target_layer.register_backward_hook(backward_hook)
    
    def generate(self, input_tensor: torch.Tensor, target_class: int = 1) -> np.ndarray:
        """Generate Grad-CAM heatmap"""
        if self.target_layer is None:
            return np.zeros((224, 224))
        
        try:
            self.model.zero_grad()
            output = self.model(input_tensor)
            
            # Backward pass for target class
            one_hot = torch.zeros_like(output)
            one_hot[0][target_class] = 1
            output.backward(gradient=one_hot, retain_graph=True)
            
            if self.gradients is None or self.activations is None:
                return np.zeros((224, 224))
            
            # Pool gradients
            weights = self.gradients.mean(dim=[2, 3], keepdim=True)
            
            # Weighted combination of activations
            cam = (weights * self.activations).sum(dim=1, keepdim=True)
            cam = torch.relu(cam)
            
            # Normalize
            cam = cam.squeeze().cpu().numpy()
            if cam.max() > 0:
                cam = (cam - cam.min()) / (cam.max() - cam.min())
            
            return cam
        
        except Exception as e:
            logger.error(f"Grad-CAM error: {e}")
            return np.zeros((224, 224))
